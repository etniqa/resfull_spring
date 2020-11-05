package com.example.mobileappws.service.impl;

import com.example.mobileappws.exceptions.UserServiceException;
import com.example.mobileappws.io.repositories.UserRepository;
import com.example.mobileappws.io.entity.UserEntity;
import com.example.mobileappws.service.UserService;
import com.example.mobileappws.shared.AmazonSES;
import com.example.mobileappws.shared.Utils;
import com.example.mobileappws.shared.dto.UserDto;
import com.example.mobileappws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

/*    @Autowired
    Utils utils;*/

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {
        if (user.getAddresses() != null) {
            user.getAddresses().forEach(addressDto -> {
                addressDto.setAddressId(Utils.generateRandomId());
                addressDto.setUser(user);
            });
        }

        ModelMapper mapper = new ModelMapper();
        UserEntity userEntity = mapper.map(user, UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setUserId(Utils.generateRandomId());
        userEntity.setEmailVerificationToken(Utils.generateEmailVerificationToken(user.getUserId()));
        userEntity.setEmailVerificationStatus(false);

        UserEntity savedUserEntity = userRepository.save(userEntity);
        UserDto returnVal = mapper.map(savedUserEntity, UserDto.class);

        new AmazonSES().verifyEmail(returnVal);

        return returnVal;
    }

    // login must work only if account was verify via email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntityByEmail = userRepository.findByEmail(email);
        if (userEntityByEmail == null) throw new UsernameNotFoundException(email);

        // User(from Spring) implements UserDetail(from Spring)
//        return new User(byEmail.getEmail(), byEmail.getEncryptedPassword(), new ArrayList<>());
        return new User(
                userEntityByEmail.getEmail(),
                userEntityByEmail.getEncryptedPassword(),
                userEntityByEmail.getEmailVerificationStatus(),
                true, true, true, new ArrayList<>());
    }

    @Override
    public UserDto getUserByEmail(String email) {
        UserEntity byEmail = userRepository.findByEmail(email);
        if (byEmail == null) throw new UsernameNotFoundException(email);
        UserDto returnVal = new UserDto();
        BeanUtils.copyProperties(byEmail, returnVal);

        return returnVal;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto returnVal = new UserDto();
        UserEntity byUserId = this.userRepository.findByUserId(userId);
        if (byUserId == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + ", userId: " + userId);
        BeanUtils.copyProperties(byUserId, returnVal);

        return returnVal;
    }

    @Override
    public UserDto updateUser(
            UserDto userDtoForUpdate,
            String userId
    ) {
        UserDto returnVal = new UserDto();
        UserEntity byUserId = this.userRepository.findByUserId(userId);

        if (byUserId == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        // ignore properties where fields is null
        Long idFromDb = byUserId.getId();
        BeanUtils.copyProperties(userDtoForUpdate, byUserId, this.getNullPropertyNames(userDtoForUpdate));
        byUserId.setId(idFromDb);
        UserEntity savedUser = this.userRepository.save(byUserId);
        BeanUtils.copyProperties(savedUser, returnVal);

        return returnVal;
    }

    // get String[] names of fields which are null
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set emptyNames = new HashSet();
        for (java.beans.PropertyDescriptor pd : pds) {
            //check if value of this property is null then add it to the collection
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];

        return (String[]) emptyNames.toArray(result);
    }

    @Override
    public void delete(String userId) {
        UserEntity byUserId = this.userRepository.findByUserId(userId);

        if (byUserId == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        this.userRepository.delete(byUserId);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnVal = new ArrayList<>();

        // pagination
        Pageable pageable = PageRequest.of(page, limit);
        Page<UserEntity> usersPage = this.userRepository.findAll(pageable);
        usersPage.getContent().stream().forEach(userEntity -> {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnVal.add(userDto);
        });

        return returnVal;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnVal = false;

        UserEntity userEntity = userRepository.findByEmailVerificationToken(token);

        if (userEntity != null) {
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(true);
                userRepository.save(userEntity);
                returnVal = true;
            } else {
                throw new UserServiceException("Token has expired");
            }
        } else {
            throw new UserServiceException("There is no user with this verification token: " + token);
        }

        return returnVal;
    }

    @Override
    public boolean requestPasswordReset(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UserServiceException("There is no user with this email: " + email);
        }

        String passwordResetToken = Utils.generatePasswordResetToken(userEntity.getUserId());
        userEntity.setPasswordResetVerificationToken(passwordResetToken);
        this.userRepository.save(userEntity);
        new AmazonSES().sendLetterToAllowResetPassword(passwordResetToken, email);

        return true;
    }

    @Override
    public boolean confirmPasswordReset(String resetPasswordVerificationToken, String newPassword) {
        UserEntity byPasswordResetVerificationToken =
                this.userRepository.findByPasswordResetVerificationToken(resetPasswordVerificationToken);
        if (byPasswordResetVerificationToken == null) {
            throw new UsernameNotFoundException("There is no user with this reset password verification token");
        }
        if (Utils.hasTokenExpired(resetPasswordVerificationToken)) {
            throw new UserServiceException("Token has expired");
        }

        byPasswordResetVerificationToken.setPasswordResetVerificationToken(null);
        byPasswordResetVerificationToken.setEncryptedPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(byPasswordResetVerificationToken);

        return true;
    }
}
