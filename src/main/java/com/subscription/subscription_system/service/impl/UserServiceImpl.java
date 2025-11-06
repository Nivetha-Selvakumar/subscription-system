package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.UserCreateRequestDto;
import com.subscription.subscription_system.dto.UserDetailsDto;
import com.subscription.subscription_system.dto.UserDetailsRequestDto;
import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.AuthTokenEntity;
import com.subscription.subscription_system.entity.SubscriberEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumUserType;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.mapper.UserMapper;
import com.subscription.subscription_system.repository.AdminRepo;
import com.subscription.subscription_system.repository.AuthTokenRepo;
import com.subscription.subscription_system.repository.SubscriberRepo;
import com.subscription.subscription_system.repository.UserRepo;
import com.subscription.subscription_system.service.UserService;
import com.subscription.subscription_system.utils.JwtUtils;
import com.subscription.subscription_system.utils.QueryUtils;
import com.subscription.subscription_system.validation.BusinessValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public AuthTokenEntity createUser(UserCreateRequestDto userCreateDto) throws CommonException {

        //check duplicate email
        businessValidation.getUserByEmail(userCreateDto.getEmail());

        // Save User
        UserEntity userEntity = userMapper.mapUserDtoToUserEntity(userCreateDto);
        userRepo.save(userEntity);

        // Step 4: Generate new JWT
        String newToken = jwtUtils.generateToken(userEntity.getEmail(), userEntity.getRole().name());

        // Step 5: Save token in Auth_Token collection
        AuthTokenEntity tokenEntity = new AuthTokenEntity();
        tokenEntity.setUser(userEntity);
        tokenEntity.setAuthToken(newToken);
        tokenEntity.setStatus(EnumStatusType.ACTIVE.getName());
        authTokenRepo.save(tokenEntity);

//       if (Objects.equals(userCreateDto.getRole(), EnumUserType.SUBSCRIBER.getValue())) {
//            SubscriberEntity subscriberEntity = new SubscriberEntity();
//            subscriberEntity.setUser(userEntity);
//            subscriberEntity.setCurrentSubStatus("Inactive");
//            subscriberEntity.setJoinDate(LocalDateTime.now().toString());
//            subscriberRepo.save(subscriberEntity);
//        }
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
    public List<UserDetailsDto> getUsersList(String userId, String search, String filterBy, String sortBy, String sortDir) throws CommonException {
        // 1️⃣ Validate Admin Access
        businessValidation.validateUserList(userId);

        // 2️⃣ Build Mongo query dynamically
        Query query = QueryUtils.buildUserQuery(search, filterBy, sortBy, sortDir);
        List<UserEntity> users = mongoTemplate.find(query, UserEntity.class);

        // 3️⃣ Preload Admin and Subscriber data
        List<AdminEntity> admins = mongoTemplate.findAll(AdminEntity.class);
        List<SubscriberEntity> subscribers = mongoTemplate.findAll(SubscriberEntity.class);

        Map<String, AdminEntity> adminMap = admins.stream()
                .filter(a -> a.getUser() != null)
                .collect(Collectors.toMap(a -> a.getUser().getId(), a -> a));

        Map<String, SubscriberEntity> subscriberMap = subscribers.stream()
                .filter(s -> s.getUser() != null)
                .collect(Collectors.toMap(s -> s.getUser().getId(), s -> s));

        // 4️⃣ Map Data
        return users.stream()
                .map(user -> {
                    AdminEntity adminEntity = adminMap.get(user.getId());
                    SubscriberEntity subscriberEntity = subscriberMap.get(user.getId());
                    return userMapper.mapUserDetails(user, adminEntity, subscriberEntity);
                })
                .toList();
    }
}
