package com.example.mobileappws.shared;

import com.example.mobileappws.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Component
public class Utils {
    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";
    private final int ITERATIONS = 1000;
    private final int KEY_LENGTH = 256;

    public static boolean hasTokenExpired(String token) {
        // claims == our decrypted token
        Claims claims = Jwts.parser()
                // set token_secret
                .setSigningKey(SecurityConstants.TOKEN_SECRET)
                .parseClaimsJws(token).getBody();

        Date tokenExpirDate = claims.getExpiration();

        return tokenExpirDate.before(new Date());
    }

    public static String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    public static String generateEmailVerificationToken(String userId) {

        return Utils.generateToken(userId,
                SecurityConstants.EXPIRATION_TIME,
                SecurityConstants.TOKEN_SECRET);
    }

    public static String generatePasswordResetToken(String userId) {

        return Utils.generateToken(userId,
                SecurityConstants.EXPIRATION_TIME,
                SecurityConstants.TOKEN_SECRET);
    }

    private static String generateToken(String subject, long expirationTime, String tokenSecret) {
        String token = Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                // using HS512 encoding on ${SecurityConstants.TOKEN_SECRET} word
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();

        return token;
    }
}
