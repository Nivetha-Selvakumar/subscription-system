package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.AuthTokenEntity;
import com.subscription.subscription_system.entity.SubscriberEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumSexType;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumSubscriptionStatus;
import com.subscription.subscription_system.enumuration.EnumUserType;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.mapper.UserMapper;
import com.subscription.subscription_system.repository.AdminRepo;
import com.subscription.subscription_system.repository.AuthTokenRepo;
import com.subscription.subscription_system.repository.SubscriberRepo;
import com.subscription.subscription_system.repository.UserRepo;
import com.subscription.subscription_system.service.UserService;
import com.subscription.subscription_system.utils.DateTimeUtils;
import com.subscription.subscription_system.utils.JwtUtils;
import com.subscription.subscription_system.utils.QueryUtils;
import com.subscription.subscription_system.validation.BusinessValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserMapper userMapper;

    @Autowired
    BusinessValidation businessValidation;

    @Autowired
    AdminRepo adminRepo;

    @Autowired
    SubscriberRepo subscriberRepo;

    @Autowired
    AuthTokenRepo authTokenRepo;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public AuthTokenEntity signUpUser(SignupRequestDto userCreateDto) throws CommonException {

        //check duplicate email
        businessValidation.getUserByEmail(userCreateDto.getEmail());

        // Save User
        UserEntity userEntity = userMapper.mapSignupDtoToUserEntity(userCreateDto, null);
        userRepo.save(userEntity);

        // Step 4: Generate new JWT
        String newToken = jwtUtils.generateToken(userEntity.getEmail(), userEntity.getRole().name());

        // Step 5: Save token in Auth_Token collection
        AuthTokenEntity tokenEntity = new AuthTokenEntity();
        tokenEntity.setUser(userEntity);
        tokenEntity.setAuthToken(newToken);
        tokenEntity.setStatus(EnumStatusType.ACTIVE.getName());
        authTokenRepo.save(tokenEntity);

        //Return user values
        return (tokenEntity);
    }

    @Override
    public UserDetailsDto getUserDetails(UserDetailsRequestDto userDetailsInput) throws CommonException {
        Optional<UserEntity> optionalUser = userRepo.findByEmailAndId(
                userDetailsInput.getEmail(),
                userDetailsInput.getUserId()
        );

        // 2️⃣ If user not found
        if (optionalUser.isEmpty()) {
            throw new CommonException("User not found with given email or ID", HttpStatus.BAD_REQUEST.value());
        }

        UserEntity user = optionalUser.get();
        String role = user.getRole().name().toLowerCase();
        AdminEntity adminEntity = new AdminEntity();
        SubscriberEntity subscriberEntity = new SubscriberEntity();
        if (role.equals(EnumUserType.ADMIN.name().toLowerCase())) {
            adminEntity = adminRepo.findByUser(user);
        } else if (role.equals(EnumUserType.SUBSCRIBER.name().toLowerCase())) {
            subscriberEntity = subscriberRepo.findByUser(user);
        }

        return userMapper.mapUserDetails(user, adminEntity, subscriberEntity);
    }

    @Override
    public PaginatedResponse<UserDetailsDto> getUsersList(String userId, String search, String filterBy, String sortBy, String sortDir, int offset, int limit) throws CommonException {
        // 1️⃣ Validate admin
        businessValidation.validateUserList(userId);

        // 2️⃣ Build main query
        Query query = QueryUtils.buildUserQuery(search, filterBy, sortBy, sortDir);

        // 3️⃣ Get total count BEFORE pagination
        long totalCount = mongoTemplate.count(query, UserEntity.class);

        // 4️⃣ Apply pagination
        query.skip(offset).limit(limit);

        // 5️⃣ Fetch paginated users
        List<UserEntity> users = mongoTemplate.find(query, UserEntity.class);

        // 6️⃣ Preload Admins & Subscribers
        List<AdminEntity> admins = mongoTemplate.findAll(AdminEntity.class);
        List<SubscriberEntity> subscribers = mongoTemplate.findAll(SubscriberEntity.class);

        Map<String, AdminEntity> adminMap = admins.stream()
                .filter(a -> a.getUser() != null)
                .collect(Collectors.toMap(a -> a.getUser().getId(), a -> a));

        Map<String, SubscriberEntity> subscriberMap = subscribers.stream()
                .filter(s -> s.getUser() != null)
                .collect(Collectors.toMap(s -> s.getUser().getId(), s -> s));

        // 7️⃣ Map all data
        List<UserDetailsDto> userDtos = users.stream()
                .map(user -> {
                    AdminEntity adminEntity = adminMap.get(user.getId());
                    SubscriberEntity subscriberEntity = subscriberMap.get(user.getId());
                    return userMapper.mapUserDetails(user, adminEntity, subscriberEntity);
                })
                .toList();

        // 8️⃣ Return data + totalCount
        return new PaginatedResponse<>(userDtos, totalCount);
    }

    @Override

    public UserDetailsDto editUser(String requesterId, String targetUserId, UserEditRequestDto editDto) throws CommonException {
        // Validate permission
        UserEntity requester = businessValidation.validateSelfOrAdmin(requesterId, targetUserId);

        Optional<UserEntity> targetUserOpt = userRepo.findById(targetUserId);
        if (targetUserOpt.isEmpty()) {
            throw new CommonException("Target user not found", HttpStatus.BAD_REQUEST.value());
        }

        UserEntity targetUser = targetUserOpt.get();

        // 2️⃣ Prevent email change (since frontend sends all fields)
        if (editDto.getEmail() != null && !editDto.getEmail().equalsIgnoreCase(targetUser.getEmail())) {
            throw new CommonException("Email field cannot be modified", HttpStatus.BAD_REQUEST.value());
        }

        // 3️⃣ Update common user fields
        if (editDto.getFirstName() != null) targetUser.setFirstName(editDto.getFirstName());
        if (editDto.getLastName() != null) targetUser.setLastName(editDto.getLastName());
        if (editDto.getAddress() != null) targetUser.setAddress(editDto.getAddress());
        if (editDto.getDateOfBirth() != null) targetUser.setDob(editDto.getDateOfBirth());
        if (editDto.getPhoneNumber() != null) targetUser.setPhoneNumber(editDto.getPhoneNumber());
        if (editDto.getSex() != null) targetUser.setSex(EnumSexType.fromValue(editDto.getSex()));
        if (editDto.getStatus() != null) targetUser.setStatus(EnumStatusType.fromValue(editDto.getStatus()));

        userRepo.save(targetUser); // save user part first

        // 4️⃣ Role-based additional updates
        String role = targetUser.getRole().getValue().toLowerCase();

        AdminEntity adminEntity = null;
        SubscriberEntity subscriberEntity = null;

        if (requester.getRole().equals(EnumUserType.ADMIN) && !Objects.equals(requester.getId(), targetUser.getId())
                && editDto.getRole() != null) {
            targetUser.setRole(EnumUserType.valueOf(editDto.getRole()));
        }


        switch (role) {
            case "admin":
                adminEntity = adminRepo.findByUser(targetUser);
                if (adminEntity == null) {
                    adminEntity = new AdminEntity();
                    adminEntity.setUser(targetUser);
                }
                adminEntity.setSalary(editDto.getSalary());
                adminRepo.save(adminEntity);
                break;

            case "subscriber":
                subscriberEntity = subscriberRepo.findByUser(targetUser);
                if (subscriberEntity == null) {
                    subscriberEntity = new SubscriberEntity();
                    subscriberEntity.setUser(targetUser);
                }
                subscriberEntity.setCurrentSubStatus(editDto.getCurrentSubStatus());
                subscriberEntity.setSubStartDate(editDto.getSubStartDate());
                subscriberEntity.setSubEndDate(editDto.getSubEndDate());
                subscriberEntity.setJoinDate(editDto.getJoinDate());
                subscriberRepo.save(subscriberEntity);
                break;

            default:
                // USER has only UserEntity fields
                break;
        }

        // 5️⃣ Return response DTO
        return userMapper.mapUserDetails(targetUser, adminEntity, subscriberEntity);
    }


    @Override
    public void deleteUser(String requesterId, String targetUserId) throws CommonException {
        // 1️⃣ Validate requester (only self or admin can delete)
        UserEntity requester = businessValidation.validateSelfOrAdmin(requesterId, targetUserId);

        // 2️⃣ Validate target user exists and active
        Optional<UserEntity> userOpt = userRepo.findByIdAndStatus(targetUserId, EnumStatusType.ACTIVE);
        if (userOpt.isEmpty()) {
            throw new CommonException("User not found or already inactive", HttpStatus.BAD_REQUEST.value());
        }

        UserEntity targetUser = userOpt.get();
        String role = targetUser.getRole().getValue().toLowerCase();

        log.info("Deleting user [{}] with role [{}]", targetUser.getEmail(), role);

        // 3️⃣ Soft delete main user (set INACTIVE)
        targetUser.setStatus(EnumStatusType.INACTIVE);
        userRepo.save(targetUser);

        // 4️⃣ Cascade delete (soft or hard) based on role
        switch (role) {
            case "admin":
                Optional<AdminEntity> adminOpt = adminRepo.findByUserAndStatus(targetUser, EnumStatusType.ACTIVE);
                if (adminOpt.isPresent()) {
                    AdminEntity adminEntity = adminOpt.get();
                    // Option 1 (Soft Delete)
                    adminEntity.setStatus(EnumStatusType.INACTIVE);
                    adminRepo.save(adminEntity);
                    log.info("Admin entity soft-deleted for user: {}", targetUser.getEmail());
                }
                break;

            case "subscriber":
                Optional<SubscriberEntity> subscriberOpt = subscriberRepo.findByUserAndStatus(targetUser, EnumStatusType.ACTIVE);
                if (subscriberOpt.isPresent()) {
                    SubscriberEntity subscriberEntity = subscriberOpt.get();
                    // Option 1 (Soft Delete)
                    subscriberEntity.setStatus(EnumStatusType.INACTIVE);
                    subscriberRepo.save(subscriberEntity);

                    log.info("Subscriber entity soft-deleted for user: {}", targetUser.getEmail());
                }
                break;

            default:
                // Normal user → only main user entity is updated
                log.info("Normal user [{}] deleted (soft)", targetUser.getEmail());
                break;
        }

        log.info("✅ User deletion completed for targetUserId: {}", targetUserId);
    }

    @Override
    @Transactional
    public UserEntity createUser(UserCreateRequestDto userCreateDto, String userId) throws CommonException {

        // User Of Admin Exist or not
        UserEntity adminUser = businessValidation.getAdminByUserId(userId);

        // Check admin user or not
        businessValidation.checkAdminOrNot(adminUser);

        //check duplicate email
        businessValidation.getUserByEmail(userCreateDto.getEmail());

        // Save User
        UserEntity userEntity = userMapper.mapUserDtoToUserEntity(userCreateDto, adminUser.getFirstName() + adminUser.getLastName());
        userRepo.save(userEntity);

        if (userCreateDto.getRole().equalsIgnoreCase(EnumUserType.ADMIN.getValue())) {
            // Create new Admin
            AdminEntity adminEntity = new AdminEntity();
            adminEntity.setUser(userEntity);
            adminEntity.setSalary(
                    userCreateDto.getSalary() != null && !userCreateDto.getSalary().isEmpty()
                            ? Double.parseDouble(userCreateDto.getSalary())
                            : 0.0
            );
            adminEntity.setStatus(EnumStatusType.ACTIVE);
            adminEntity.setCreatedBy(adminUser.getFirstName() + adminUser.getLastName());
            adminEntity.setUpdatedBy(adminUser.getFirstName() + adminUser.getLastName());
            adminEntity.setCreatedAt(LocalDateTime.now());
            adminEntity.setUpdatedAt(LocalDateTime.now());

            adminRepo.save(adminEntity);
        } else if (userCreateDto.getRole().equalsIgnoreCase(EnumUserType.SUBSCRIBER.getValue())) {
            SubscriberEntity subscriberEntity = new SubscriberEntity();
            subscriberEntity.setUser(userEntity);
            subscriberEntity.setCurrentSubStatus(EnumSubscriptionStatus.fromValue(userCreateDto.getCurrentSubStatus()).getName());
            DateTimeFormatter storeFormatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("yyyy-MMM-dd")
                    .toFormatter(Locale.ENGLISH);

            if (userCreateDto.getSubStartDate() != null && !userCreateDto.getSubStartDate().isEmpty()) {
                LocalDate startDate = DateTimeUtils.parseFlexibleDate(userCreateDto.getSubStartDate());
                // store as yyyy-MMM-dd (upper/lowercase depends on formatter, use uppercase for month)
                subscriberEntity.setSubStartDate(startDate.format(storeFormatter).toUpperCase(Locale.ENGLISH));
            }

            if (userCreateDto.getSubEndDate() != null && !userCreateDto.getSubEndDate().isEmpty()) {
                LocalDate endDate = DateTimeUtils.parseFlexibleDate(userCreateDto.getSubEndDate());
                subscriberEntity.setSubEndDate(endDate.format(storeFormatter).toUpperCase(Locale.ENGLISH));
            }

            if (userCreateDto.getJoinDate() != null && !userCreateDto.getJoinDate().isEmpty()) {
                LocalDate joinDate = DateTimeUtils.parseFlexibleDate(userCreateDto.getJoinDate());
                subscriberEntity.setSubEndDate(joinDate.format(storeFormatter).toUpperCase(Locale.ENGLISH));
            }

            subscriberEntity.setCreatedBy(adminUser.getFirstName() + adminUser.getLastName());
            subscriberEntity.setUpdatedBy(adminUser.getFirstName() + adminUser.getLastName());
            subscriberEntity.setCreatedAt(LocalDateTime.now());
            subscriberEntity.setUpdatedAt(LocalDateTime.now());
            subscriberEntity.setStatus(EnumStatusType.ACTIVE);

            subscriberRepo.save(subscriberEntity);
        }

        return userEntity;
    }
}
