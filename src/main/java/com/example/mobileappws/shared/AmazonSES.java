package com.example.mobileappws.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.example.mobileappws.shared.dto.UserDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AmazonSES {
    static final String FROM = "etniqaaa@gmail.com";
    static final String SUBJECT = "Verification PhotoApp";

    // The parts for the verification email
    static final String HTML_BODY_EMAIL_VERIFY_LOCAL_TOMCAT = "<h1>Verification from MobileApp</h1>"
            + "<p>To complete registration process click on link below: " +
            " <p><a href='http://localhost:8080/mobile-app_verification/email_verification.html?" +
            "token=$tokenVal'>Click me </a>";
    static final String TEXT_BODY_EMAIL_VERIFY_LOCAL_TOMCAT = "Verification" +
            "To complete registration process open link below: " +
            "http://localhost:8080/mobile-app_verification/email_verification.html?" +
            "token=$tokenVal>";

    static final String HTML_BODY_EMAIL_VERIFY_AWS_TOMCAT = "<h1>Verification from MobileApp</h1>"
            + "<p>To complete registration process click on link below: " +
            " <p><a href='http://ec2-35-180-103-161.eu-west-3.compute.amazonaws.com:8080/mobile-app_verification/email_verification.html?" +
            "token=$tokenVal'>Click me </a>";
    static final String TEXT_BODY_EMAIL_VERIFY_AWS_TOMCAT = "Verification" +
            "To complete registration process open link below: " +
            "http://ec2-35-180-103-161.eu-west-3.compute.amazonaws.com:8080//mobile-app_verification/email_verification.html?" +
            "token=$tokenVal>";


    // The parts for the reset password verification
    static final String HTML_BODY_RESET_PASSWORD_VERIFY_LOCAL_TOMCAT = "<h1>Reset password from MobileApp</h1>"
            + "<p>If you want to reset your password on MobileApp please " +
            " <p><a href='http://localhost:8080/mobile-app_verification/reset_password_verification.html?" +
            "token=$tokenVal'>click here</a>";
    static final String TEXT_BODY_RESET_PASSWORD_VERIFY_LOCAL_TOMCAT = "Reset password from MobileApp" +
            "If you want to reset your password on MobileApp please open link below: " +
            "http://localhost:8080/mobile-app_verification/reset_password_verification.html?" +
            "token=$tokenVal";

    static final String HTML_BODY_RESET_PASSWORD_VERIFY_AWS_TOMCAT = "<h1>Reset password from MobileApp</h1>"
            + "<p>If you want to reset your password on MobileApp please " +
            " <p><a href='http://ec2-35-180-103-161.eu-west-3.compute.amazonaws.com:8080/mobile-app_verification/reset_password_verification.html?" +
            "token=$tokenVal'>click here</a>";
    static final String TEXT_BODY_RESET_PASSWORD_VERIFY_AWS_TOMCAT = "Reset password from MobileApp" +
            "If you want to reset your password on MobileApp please open link below: " +
            "http://ec2-35-180-103-161.eu-west-3.compute.amazonaws.com:8080/mobile-app_verification/reset_password_verification.html?" +
            "token=$tokenVal";

    public void verifyEmail(UserDto userDto) {
        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.EU_WEST_2).build();
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8")
                                .withData(HTML_BODY_EMAIL_VERIFY_AWS_TOMCAT.replace("$tokenVal", userDto.getEmailVerificationToken())))
                                .withText(new Content().withCharset("UTF-8")
                                        .withData(TEXT_BODY_EMAIL_VERIFY_AWS_TOMCAT.replace("$tokenVal", userDto.getEmailVerificationToken()))))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);

        client.sendEmail(request);
    }

    public void sendLetterToAllowResetPassword(String token, String email) {
        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.EU_WEST_2).build();
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8")
                                .withData(HTML_BODY_RESET_PASSWORD_VERIFY_AWS_TOMCAT.replace("$tokenVal", token)))
                                .withText(new Content().withCharset("UTF-8")
                                        .withData(TEXT_BODY_RESET_PASSWORD_VERIFY_AWS_TOMCAT.replace("$tokenVal", token))))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);

        client.sendEmail(request);
    }
}
