package com.example.mobileappws.service;

import com.example.mobileappws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByEmail(String email);
    UserDto getUserByUserId(String userId);
    UserDto updateUser(UserDto userDtoForUpdate, String userId);
    void delete(String userId);
    List<UserDto> getUsers(int page, int limit);

    boolean verifyEmailToken(String token);
    boolean requestPasswordReset(String email);

    boolean confirmPasswordReset(String passwordResetToken, String newPassword);
}