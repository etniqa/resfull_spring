package com.example.mobileappws.security;

import com.example.mobileappws.SpringApplicationContext;
import com.example.mobileappws.service.UserService;
import com.example.mobileappws.shared.dto.UserDto;
import com.example.mobileappws.ui.model.request.UserLoginRequestModel;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // this method trigger when we will get try to authenticate
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {
        try {
            // read from request (in JSON) credentials of user during authentication
            UserLoginRequestModel creds = new ObjectMapper()
                    .readValue(request.getInputStream(), UserLoginRequestModel.class);

            return authenticationManager.authenticate(
                    // after that Spring will do all stuff automatically
                    // (using, for example, loadByUsername()(query to DB) in UserServiceImpl (implements (UserService extends UserDetailsService(spring))) )
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Autowired
    SpringApplicationContext springApplicationContext;

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult   // Authentication which we got in previous method (attemptAuthentication())
    ) throws IOException, ServletException {
        System.out.println("starts successfulAuthentication()");

        String email = ((User)authResult.getPrincipal()).getUsername();
        // make token for frontend
        String token = Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                // using HS512 encoding on ${SecurityConstants.TOKEN_SECRET} word
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.TOKEN_SECRET)
                .compact();

        // we can just write @Autowired, but we also can beans from Context
        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
        UserDto userByEmail = userService.getUserByEmail(email);

        // add in header Authorization: "Beaver token"
        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        response.addHeader("userId", userByEmail.getUserId());
    }
}