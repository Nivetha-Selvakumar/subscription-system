package com.subscription.subscription_system.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    private String id;

    @Field("emp_id")
    private String empId;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("email")
    private String email;

    @Field("address")
    private String address;

    @Field("DOB")
    private String dob;

    @Field("phone_number")
    private String phoneNumber;

    @Field("sex")
    private String sex;

    @Field("role")
    private String role;
}
