package com.example.mobileappws.ui.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserLoginRequestModel {
    private String email;
    private String password;
}
