package com.example.mobileappws.exceptions;

import com.example.mobileappws.service.UserService;
import com.example.mobileappws.ui.model.response.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice // can handle exception (like filter (middleware))
public class AppExceptionsHandler {
    //    @ExceptionHandler(value = {UserServiceException.class}) === will handle throwing UserServiceException
    //    you can add other class here: value = {UserServiceException.class, ....}
    @ExceptionHandler(value = {UserServiceException.class})
    public ResponseEntity<Object> handleUserServiceException(
            UserServiceException e,
            WebRequest req
    ) {
        ErrorMessage errorMessage = new ErrorMessage(new Date(), e.getMessage());
        System.out.println("UserServiceException");

        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleOtherException(
            Exception e,
            WebRequest req
    ) {
        ErrorMessage errorMessage = new ErrorMessage(new Date(), e.getMessage());
        System.out.println("Exception");

        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
