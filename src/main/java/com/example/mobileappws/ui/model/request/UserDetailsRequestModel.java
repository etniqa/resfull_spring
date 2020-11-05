package com.example.mobileappws.ui.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsRequestModel {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private List<AddressRequestModel> addresses;
}
