package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends MongoRepository<UserEntity, String> {
    // Find by email or empId
    UserEntity findByEmailOrEmpId(String email,String empId);

    // Find by email
    UserEntity findByEmail(String email);

    // Optional: other custom finders
    UserEntity findByEmpId(String empId);

    // Or list by role (Admin / Subscriber)
    List<UserEntity> findAllByRole(String role);

}
