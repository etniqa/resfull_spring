package com.example.mobileappws.io.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntity implements Serializable {
    private static final long serialVersionUID = -8793488453596502822L;

    @Id
    @GeneratedValue
    private Long id;    // in DB

    @Column(nullable = false)
    private String addressId; // public id

    @Column(length = 100, nullable = false)
    private String city;
    @Column(length = 100, nullable = false)
    private String country;
    @Column(nullable = false)
    private String streetName;
    @Column(length = 7, nullable = false)
    private String postalCode;
    @Column(length = 10, nullable = false)
    private String type;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "users_id")
    private UserEntity user;
}
