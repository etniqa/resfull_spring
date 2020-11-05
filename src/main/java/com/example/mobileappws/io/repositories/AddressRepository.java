package com.example.mobileappws.io.repositories;

import com.example.mobileappws.io.entity.AddressEntity;
import com.example.mobileappws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
    List<AddressEntity> findAllByUser(UserEntity userDetails);
    AddressEntity findByUserAndAddressId(UserEntity userDetails, String addressId);

}
