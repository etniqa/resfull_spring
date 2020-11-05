package com.example.mobileappws.io.repositories;

import com.example.mobileappws.io.entity.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

// public interface UserRepository extends CrudRepository<UserEntity, Long>  was before we start to use pagination
@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    UserEntity findByUserId(String userId);
    UserEntity findByEmailVerificationToken(String emailVerificationToken);
    UserEntity findByPasswordResetVerificationToken(String passwordResetVerificationToken);
}
