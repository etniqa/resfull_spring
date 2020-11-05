package com.example.mobileappws.ui.controller;

import com.example.mobileappws.exceptions.AddressServiceException;
import com.example.mobileappws.exceptions.UserServiceException;
import com.example.mobileappws.service.AddressService;
import com.example.mobileappws.service.UserService;
import com.example.mobileappws.shared.dto.AddressDto;
import com.example.mobileappws.shared.dto.UserDto;
import com.example.mobileappws.ui.model.request.PasswordResetRequestModel;
import com.example.mobileappws.ui.model.request.UserDetailsRequestModel;
import com.example.mobileappws.ui.model.response.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private AddressService addressService;

    @Autowired
    public UserController(
            UserService userService,
            AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

    @PostMapping(
            path = "/reset_password",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel resetPasswordRequest(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName("password reset request");

        boolean isOperationSuccess = userService.requestPasswordReset(passwordResetRequestModel.getEmail());
        returnValue.setOperationStatus(isOperationSuccess ? "success" : "error");

        return returnValue;
    }

    //  produces = MediaType.APPLICATION_XML_VALUE === return in XML or JSON
    //    consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE} === get in XML or JSON
    @GetMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUser(@PathVariable("id") String userId) {
        System.out.println("start controller for getUser");

        UserRest returnVal = new UserRest();
        UserDto userByUserId = userService.getUserByUserId(userId);
        BeanUtils.copyProperties(userByUserId, returnVal);

        return returnVal;
    }

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "2") int limit) {
        page -= 1;
        System.out.println("start controller for getUsers");

        List<UserRest> returnVal = new ArrayList<>();
        List<UserDto> userDtos = this.userService.getUsers(page, limit);

        userDtos.stream().forEach(userDto -> {
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(userDto, userRest);
            returnVal.add(userRest);
        });

        return returnVal;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
        System.out.println("start controller for createUser");

        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        ModelMapper mapper = new ModelMapper();
        UserDto userDto = mapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        UserRest returnValue = mapper.map(createdUser, UserRest.class);

        return returnValue;
    }

    @PutMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest updateUser(
            @RequestBody UserDetailsRequestModel userDetails,
            @PathVariable("id") String userId) {
        System.out.println("start controller for updateUser");

        UserRest returnVal = new UserRest();
        UserDto userDtoForUpdate = new UserDto();
        BeanUtils.copyProperties(userDetails, userDtoForUpdate);

        UserDto userDtoUpdated = this.userService.updateUser(userDtoForUpdate, userId);
        BeanUtils.copyProperties(userDtoUpdated, returnVal);

        return returnVal;
    }

    @DeleteMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable("id") String userId) {
        System.out.println("start controller for deleteUser");

        this.userService.delete(userId);

        return new OperationStatusModel(ResponseMessages.ResponseStatuses.SUCCESS.toString(), ResponseMessages.DELETE.toString());
    }


    // "application/hal+json" === for support Hypertext Application Language
    // CollectionModel instead of List for HAL-response (EntityModel instead of object)
    @GetMapping(
            path = "/{userId}/addresses",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json"})
    public CollectionModel<AddressRest> getAddressesForUser(@PathVariable("userId") String userId) {
        System.out.println("start controller for getAddressesForUser");

        List<AddressRest> returnVal = new ArrayList<>();
        List<AddressDto> addressesDto = this.addressService.getAllAddressesForUser(userId);
        if (addressesDto != null && !addressesDto.isEmpty()) {
            // "class" for mapping list (a-la array of .class)
            java.lang.reflect.Type listType = new TypeToken<List<AddressRest>>() {
            }.getType();
            returnVal = new ModelMapper().map(addressesDto, listType);
            returnVal = returnVal.stream().map(addressRest -> addressRest.add(
                    linkTo(methodOn(UserController.class).getAddressForUser(userId, addressRest.getAddressId()))
                            .withSelfRel(),
                    linkTo(methodOn(UserController.class).getUser(userId))
                            .withRel("user"),
                    linkTo(methodOn(UserController.class).getAddressesForUser(userId))
                            .withRel("addresses")
            )).collect(Collectors.toList());
        }

        return CollectionModel.of(returnVal);
    }

    @GetMapping(
            path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public EntityModel<AddressRest> getAddressForUser(
            @PathVariable("userId") String userId,
            @PathVariable("addressId") String addressId
    ) {
        System.out.println("start controller for getAddressForUser");
        ModelMapper mapper = new ModelMapper();

        // for HATEOAS
        // methodOn(UserController.class).getUser(userId) === we get link which on getAddressForUser endpoint
        // instead of making it manually (with .slash())
        Link selfRelLink = linkTo(methodOn(UserController.class).getAddressForUser(userId, addressId))
                .withSelfRel();
        Link addressesLink = linkTo(methodOn(UserController.class).getAddressesForUser(userId))
                .withRel("addresses");
        Link userLink = linkTo(methodOn(UserController.class).getUser(userId))
                .withRel("user");

        AddressDto addressDto = this.addressService.getAddressForUser(userId, addressId);
        if (addressDto == null) throw new AddressServiceException("There is no address with userId: " + userId +
                "and addressId: " + addressId);
        AddressRest returnVal = mapper.map(addressDto, AddressRest.class);
        returnVal.add(selfRelLink, addressesLink, userLink);

        return EntityModel.of(returnVal);
    }
}
