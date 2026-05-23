package com.javaweb.service.impl;

import org.modelmapper.ModelMapper;
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

    @Autowired
    private ModelMapper modelMapper; // Inject ModelMapper đã được cấu hình

    @Override
    public UserDTO login(String email, String password_hash) {
        UserEntity entity = userRepository.findByEmailAndPassword(email, password_hash);
        if (entity != null) {
            /* * Thay vì dùng các hàm set thủ công như dto.setUserId(entity.getUser_id()),
             * ta dùng modelMapper.map() để tự động chuyển đổi toàn bộ các trường tương ứng.
             */
            return modelMapper.map(entity, UserDTO.class);
        }
        return null;
    }

    @Override
    public boolean register(UserDTO userDTO) {
        if (userRepository.checkEmailExists(userDTO.getEmail())) {
            return false; // Email đã tồn tại
        }

        // Chuyển đổi ngược từ DTO sang Entity để lưu xuống Database
        UserEntity entity = modelMapper.map(userDTO, UserEntity.class);
        
        /* * Sau khi map tự động, ta chỉ cần set thêm các giá trị mặc định 
         * mà DTO không có (ví dụ: role_id mặc định cho khách hàng là 2).
         */
        entity.setPassword_hash(userDTO.getPassword()); // Giữ nguyên theo logic cũ của bạn
        entity.setRole_id(2); 
        
        return userRepository.register(entity);
    }
}