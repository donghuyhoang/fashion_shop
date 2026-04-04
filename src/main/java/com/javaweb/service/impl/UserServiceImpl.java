package com.javaweb.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaweb.repository.UserRepository;
import com.javaweb.repository.entity.UserEntity;
import com.javaweb.service.UserService;
import model.UserDTO;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDTO login(String email, String password_hash) {
        UserEntity entity = userRepository.findByEmailAndPassword(email, password_hash);
        if (entity != null) {
            UserDTO dto = new UserDTO();
            dto.setUserId(entity.getUser_id());
            dto.setEmail(entity.getEmail());
            dto.setFullName(entity.getFullname());
            dto.setRoleId(entity.getRole_id());
            return dto;
        }
        return null;
    }
}
