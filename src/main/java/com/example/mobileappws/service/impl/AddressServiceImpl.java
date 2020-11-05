package com.example.mobileappws.service.impl;

import com.example.mobileappws.exceptions.AddressServiceException;
import com.example.mobileappws.exceptions.UserServiceException;
import com.example.mobileappws.io.entity.AddressEntity;
import com.example.mobileappws.io.entity.UserEntity;
import com.example.mobileappws.io.repositories.AddressRepository;
import com.example.mobileappws.io.repositories.UserRepository;
import com.example.mobileappws.service.AddressService;
import com.example.mobileappws.shared.dto.AddressDto;
import com.example.mobileappws.ui.model.response.ErrorMessages;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {
    private UserRepository userRepository;
    private AddressRepository addressRepository;

    @Autowired
    public AddressServiceImpl(
            UserRepository userRepository,
            AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public List<AddressDto> getAllAddressesForUser(String userId) throws AddressServiceException {
        List<AddressDto> returnVal = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();

        UserEntity byUserIdUserEntity = this.userRepository.findByUserId(userId);
        if (byUserIdUserEntity == null)
            throw new AddressServiceException("There is no userEntity with this userId: " + userId);

        List<AddressEntity> byUserDetailsAddressEntityList = this.addressRepository.findAllByUser(byUserIdUserEntity);
        if (byUserDetailsAddressEntityList == null)
            throw new AddressServiceException("There is no addresses for user with userId: " + userId);
        byUserDetailsAddressEntityList.forEach(addressEntity -> {
            returnVal.add(mapper.map(addressEntity, AddressDto.class));
        });

        return returnVal;
    }

    @Override
    public AddressDto getAddressForUser(String userId, String addressId) {
        ModelMapper mapper = new ModelMapper();

        UserEntity byUserIdUserEntity = this.userRepository.findByUserId(userId);
        if (byUserIdUserEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + " userId: " + userId);

        AddressEntity byUserDetailsAndAddressIdAddressEntity = this.addressRepository.findByUserAndAddressId(byUserIdUserEntity, addressId);
        if (byUserDetailsAndAddressIdAddressEntity == null)
            throw new AddressServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + " addressId: " + addressId);
        AddressDto returnVal = mapper.map(byUserDetailsAndAddressIdAddressEntity, AddressDto.class);

        return returnVal;
    }
}
