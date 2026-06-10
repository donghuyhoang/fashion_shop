package com.javaweb.repository;
import com.javaweb.repository.entity.UserEntity;

public interface UserRepository {
    UserEntity findByEmail(String email);

    boolean checkEmailExists(String email);
    boolean register(UserEntity user);
}
