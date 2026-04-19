package com.javaweb.service.impl;
import org.mindrot.jbcrypt.BCrypt;
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
    public UserDTO login(String email, String rawPassword){
    	if(rawPassword == null || rawPassword.isEmpty()) {
    		return null;
    	}
        UserEntity entity = userRepository.findByEmail(email);
        if (entity != null && BCrypt.checkpw(rawPassword,entity.getPassword_hash())) {
            UserDTO dto = new UserDTO();
            dto.setUserId(entity.getUser_id());
            dto.setEmail(entity.getEmail());
            dto.setFullName(entity.getFullname());
            dto.setRoleId(entity.getRole_id());
            return dto;
        }
        return null;
    }

    @Override
    public boolean register(UserDTO userDTO) 
    {
        if(userRepository.checkEmailExists(userDTO.getEmail())) {
            return false; // Email đã tồn tại
        }

        UserEntity entity = new UserEntity();
        entity.setFullname(userDTO.getFullName());
        entity.setEmail(userDTO.getEmail());
        entity.setPhone(userDTO.getPhoneNumber());
        String hashed = BCrypt.hashpw(userDTO.getPassword(),BCrypt.gensalt(12)); // hash password
        entity.setPassword_hash(hashed);
        entity.setRole_id(2); // Gán role_id mặc định cho người dùng mới (ví dụ: 2 là khách hàng)
        
        return userRepository.register(entity);
    }
}
