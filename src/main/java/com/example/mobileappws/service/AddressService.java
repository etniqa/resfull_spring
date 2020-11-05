package com.example.mobileappws.service;

import com.example.mobileappws.exceptions.AddressServiceException;
import com.example.mobileappws.shared.dto.AddressDto;
import com.example.mobileappws.ui.model.response.AddressRest;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAllAddressesForUser(String userId);
    AddressDto getAddressForUser(String userId, String addressId);
}
