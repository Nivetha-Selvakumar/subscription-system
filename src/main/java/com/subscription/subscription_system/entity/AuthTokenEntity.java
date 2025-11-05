package com.subscription.subscription_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Auth_Token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenEntity {

    @Id
    private String id;

    @DBRef
    @Field("user_id")
    private UserEntity user;

    @Field("auth_token")
    private String authToken;

    @Field("status")
    private String status;

}
