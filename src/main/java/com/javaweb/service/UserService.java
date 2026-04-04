package com.javaweb.service;
import model.UserDTO;

public interface UserService {
    UserDTO login(String email, String password_hash);
    boolean register(UserDTO userDTO);
}
