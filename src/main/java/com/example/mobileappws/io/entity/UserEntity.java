package com.example.mobileappws.io.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity(name = "users")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserEntity implements Serializable {
    private static final long serialVersionUID = 4865968939190150223L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;
    @Column(nullable = false, length = 50)
    private String firstName;
    @Column(nullable = false, length = 50)
    private String lastName;
    @Column(nullable = false, length = 100, unique = true)
    private String email;
    @Column(nullable = false)
    private String encryptedPassword;
    private String emailVerificationToken;
    private String passwordResetVerificationToken;
    @Column(nullable = false)
    private Boolean emailVerificationStatus = false;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // mappedBy = "userDetails" === name of UserEntity in AddressEntity
    private List<AddressEntity> addresses;
}
