package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.LoginRequestDto;
import com.subscription.subscription_system.entity.AuthTokenEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.mapper.UserMapper;
import com.subscription.subscription_system.repository.AuthTokenRepo;
import com.subscription.subscription_system.repository.UserRepo;
import com.subscription.subscription_system.service.AuthService;
import com.subscription.subscription_system.utils.JwtUtils;
import com.subscription.subscription_system.validation.BusinessValidation;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    BusinessValidation businessValidation;

    @Autowired
    AuthTokenRepo authTokenRepo;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public AuthTokenEntity loggingUser(LoginRequestDto loginRequestDto) throws CommonException {
        UserEntity user = userRepo.findByEmail(loginRequestDto.getEmail());
        if (user == null) {
            throw new CommonException("User does not exist", HttpStatus.CONFLICT.value());
        }

        if (!user.getPassword().equals(loginRequestDto.getPassword())) {
            throw new CommonException("Password doesn't match", HttpStatus.BAD_REQUEST.value());
        }

        // Step 3: Check for existing active token
//        Optional<AuthTokenEntity> existingActiveToken =
//                authTokenRepo.findByUserAndStatus(user, EnumStatusType.ACTIVE.getName());

//        if (existingActiveToken.isPresent()) {
//            existingActiveToken.get().setStatus(EnumStatusType.INACTIVE.getName());
//            authTokenRepo.save(existingActiveToken.get());
//        }

        List<AuthTokenEntity> activeTokens =
                authTokenRepo.findAllByUserAndStatus(user, EnumStatusType.ACTIVE.getName());
        if(!activeTokens.isEmpty()){
            activeTokens.forEach(t -> t.setStatus(EnumStatusType.INACTIVE.getName()));
            authTokenRepo.saveAll(activeTokens);
        }

        // Step 4: Generate new JWT
        String newToken = jwtUtils.generateToken(user.getEmail(), user.getRole().name());

        // Step 5: Save token in Auth_Token collection
        AuthTokenEntity tokenEntity = new AuthTokenEntity();
        tokenEntity.setUser(user);
        tokenEntity.setAuthToken(newToken);
        tokenEntity.setStatus(EnumStatusType.ACTIVE.getName());
        authTokenRepo.save(tokenEntity);

        // Step 6: Return user
        return tokenEntity;
    }

    @Override
    public void logoutUser(String authHeader) throws CommonException {
        log.info("🔒 Logout request received");

        // 🔹 1. Validate header
        if (authHeader == null || authHeader.isBlank()) {
            throw new CommonException("Authorization header missing", HttpStatus.BAD_REQUEST.value());
        }

        // Support both formats: “Bearer token” or raw token
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

        try {
            // 🔹 2. Validate token & extract claims
            Claims claims = jwtUtils.validateToken(token);
            String email = claims.getSubject();

            // 🔹 3. Check if user exists
            UserEntity user = userRepo.findByEmail(email);
            if (user == null) {
                throw new CommonException("User not found", HttpStatus.NOT_FOUND.value());
            }

//            // 🔹 4. Find active auth token for user
//            Optional<AuthTokenEntity> activeToken = authTokenRepo.findByUserAndStatus(user, EnumStatusType.ACTIVE.getName());

            List<AuthTokenEntity> activeTokens =
                    authTokenRepo.findAllByUserAndStatus(user, EnumStatusType.ACTIVE.getName());

            if (activeTokens.isEmpty()) {
                log.warn("⚠️ No active session found for user {}", email);
                throw new CommonException("No active session found for this user", HttpStatus.BAD_REQUEST.value());
            } else {
                activeTokens.forEach(t -> t.setStatus(EnumStatusType.INACTIVE.getName()));
                authTokenRepo.saveAll(activeTokens);

                log.info("✅ {} token(s) deactivated for user {}", activeTokens.size(), email);
            }

        } catch (ExpiredJwtException e) {
            log.warn("⚠️ Token expired during logout: {}", e.getMessage());
            throw new CommonException("Token has expired. Please log in again.", HttpStatus.UNAUTHORIZED.value());
        } catch (JwtException e) {
            log.error("❌ Invalid JWT: {}", e.getMessage());
            throw new CommonException("Invalid token provided", HttpStatus.UNAUTHORIZED.value());
        } catch (Exception e) {
            log.error("❌ Unexpected error during logout", e);
            throw new CommonException("Logout failed", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


}
