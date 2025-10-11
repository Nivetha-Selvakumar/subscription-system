package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends MongoRepository<UserEntity, String> {

}
