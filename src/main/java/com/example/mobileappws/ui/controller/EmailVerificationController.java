package com.example.mobileappws.ui.controller;

import com.example.mobileappws.service.UserService;
import com.example.mobileappws.ui.model.response.OperationStatusModel;
import com.example.mobileappws.ui.model.response.ResponseMessages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/email_verification")
public class EmailVerificationController {
    @Autowired
    UserService userService;

    // get request after uses click on verification link from mail
    // /email_verification?token=tokenValue
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public OperationStatusModel emailVerify(@RequestParam(value = "token") String token) {
        System.out.println("endpoint emailVerify start");

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName("Verification using email");

        boolean isVerified = userService.verifyEmailToken(token);

        returnValue.setOperationStatus(
                isVerified ? ResponseMessages.ResponseStatuses.SUCCESS.toString() :
                        ResponseMessages.ResponseStatuses.ERROR.toString());

        return returnValue;
    }
}
