package com.example.mobileappws.ui.controller;

import com.example.mobileappws.service.UserService;
import com.example.mobileappws.ui.model.request.NewPasswordsModel;
import com.example.mobileappws.ui.model.request.PasswordResetRequestModel;
import com.example.mobileappws.ui.model.response.OperationStatusModel;
import com.example.mobileappws.ui.model.response.ResponseMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/reset_password")
public class ResetPasswordController {
    @Autowired
    UserService userService;

    @GetMapping(
            path = "/request",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel resetPasswordRequest(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName("password reset request");

        boolean isOperationSuccess = userService.requestPasswordReset(passwordResetRequestModel.getEmail());
        returnValue.setOperationStatus(isOperationSuccess ? "success" : "error");

        return returnValue;
    }

    @GetMapping(
            path = "/confirm",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel resetPasswordConfirm(
            @RequestParam("token") String resetPasswordToken,
            @RequestBody @Valid NewPasswordsModel newPasswordsModel) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName("password reset confirming");

        if (!newPasswordsModel.checkEqualityOfFields()) {
            returnValue.setOperationStatus("error");

            return returnValue;
        }

        boolean isOperationSuccess = userService.confirmPasswordReset(resetPasswordToken, newPasswordsModel.getNewPassword());
        returnValue.setOperationStatus(isOperationSuccess ?
                ResponseMessages.ResponseStatuses.SUCCESS.toString() :
                ResponseMessages.ResponseStatuses.ERROR.toString());

        return returnValue;
    }
}
