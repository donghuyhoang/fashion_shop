package com.javaweb.repository;
import com.javaweb.repository.entity.UserEntity;

public interface UserRepository {
    UserEntity findByEmailAndPassword(String email, String password_hash);
}
