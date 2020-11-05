package com.example.mobileappws.security;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 8640000;   //
    public static final String TOKEN_PREFIX = "Beaver ";    //
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_SECRET = "jf9u4jgu83nf10";
    public static final String SIGN_UP_URL = "/users";
    public static final String VERIFICATION_EMAIL_URL = "/email_verification";
    public static final String RESET_PASSWORD_REQUEST_URL = "/reset_password/request";
    public static final String RESET_PASSWORD_CONFIRM_URL = "/reset_password/confirm";
}
