package com.javaweb.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javaweb.repository.impl.UserRepositoryImpl;
import com.javaweb.repository.entity.UserEntity;
import model.UserDTO;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserAPI {

    @Autowired
    private UserRepositoryImpl userRepository; 

    // ==========================================
    // API ĐĂNG NHẬP
    // ==========================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        UserEntity user = userRepository.findByEmailAndPassword(userDTO.getEmail(), userDTO.getPassword());
        if (user != null) {
            UserDTO responseDTO = new UserDTO();
            responseDTO.setUserId(user.getUser_id());
            responseDTO.setFullName(user.getFullname());
            responseDTO.setEmail(user.getEmail());
            responseDTO.setRoleId(user.getRole_id());
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.status(401).body("Email hoặc mật khẩu không chính xác!");
    }

    // ==========================================
    // API ĐĂNG KÝ
    // ==========================================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        if (userDTO.getPhoneNumber() == null || userDTO.getPhoneNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Số điện thoại là bắt buộc!");
        }
        
        if (userRepository.checkEmailExists(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã tồn tại!");
        }
        
        UserEntity newUser = new UserEntity();
        newUser.setFullname(userDTO.getFullName());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPhone(userDTO.getPhoneNumber());
        newUser.setPassword_hash(userDTO.getPassword());
        newUser.setRole_id(2); // Mặc định role_id = 2 (Khách hàng)
        
        if (userRepository.register(newUser)) {
            return ResponseEntity.ok("Đăng ký thành công!");
        }
        return ResponseEntity.internalServerError().body("Đăng ký thất bại!");
    }

    // Lấy thông tin người dùng
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Integer id) {
        UserEntity entity = userRepository.findById(id);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        
        UserDTO dto = new UserDTO();
        dto.setUserId(entity.getUser_id());
        dto.setFullName(entity.getFullname());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhone());
        dto.setRoleId(entity.getRole_id());
        
        return ResponseEntity.ok(dto);
    }

    // Cập nhật thông tin người dùng
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUserProfile(@PathVariable Integer id, @RequestBody UserDTO userDTO) {
        UserEntity entity = new UserEntity();
        entity.setUser_id(id);
        entity.setFullname(userDTO.getFullName());
        entity.setPhone(userDTO.getPhoneNumber());
        entity.setEmail(userDTO.getEmail()); 

        boolean isSuccess = userRepository.updateUser(entity);
        if (isSuccess) {
            return ResponseEntity.ok("Cập nhật hồ sơ thành công!");
        } else {
            return ResponseEntity.badRequest().body("Cập nhật thất bại!");
        }
    }

    // ==========================================
    // API ĐỔI MẬT KHẨU
    // ==========================================
    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Integer id, @RequestBody java.util.Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        
        boolean isSuccess = userRepository.changePassword(id, oldPassword, newPassword);
        if (isSuccess) {
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        }
        return ResponseEntity.badRequest().body("Mật khẩu hiện tại không chính xác!");
    }
}