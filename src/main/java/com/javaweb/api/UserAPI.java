package com.javaweb.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javaweb.repository.impl.UserRepositoryImpl;
import com.javaweb.repository.entity.UserEntity;
import model.UserDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        if (userDTO.getEmail() == null || userDTO.getPassword() == null || userDTO.getEmail().trim().isEmpty() || userDTO.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng nhập đầy đủ email và mật khẩu!");
        }

        UserEntity user = userRepository.findByEmailAndPassword(userDTO.getEmail(), userDTO.getPassword());
        if (user != null) {
            // Kiểm tra an toàn để tránh lỗi NullPointerException
            if (user.getIs_active() != null && user.getIs_active() == 0) {
                return ResponseEntity.status(403).body("Tài khoản của bạn đã bị khóa! Vui lòng liên hệ Admin.");
            }

            UserDTO responseDTO = new UserDTO();
            responseDTO.setUserId(user.getUser_id());
            responseDTO.setFullName(user.getFullname());
            responseDTO.setEmail(user.getEmail());
            responseDTO.setRoleId(user.getRole_id());
            responseDTO.setIsActive(user.getIs_active());
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.status(401).body("Email hoặc mật khẩu không chính xác!");
    }

    // ==========================================
    // API ĐĂNG KÝ
    // ==========================================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        if (userDTO.getFullName() == null || userDTO.getFullName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Họ và tên là bắt buộc!");
        }
        
        if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email là bắt buộc!");
        }
        
        if (userDTO.getPhoneNumber() == null || userDTO.getPhoneNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Số điện thoại là bắt buộc!");
        }
        
        if (userRepository.checkEmailExists(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã tồn tại!");
        }
        
        if (userRepository.checkPhoneExists(userDTO.getPhoneNumber())) {
            return ResponseEntity.badRequest().body("Số điện thoại này đã được đăng ký!");
        }
        
        String password = userDTO.getPassword();
        if (password == null || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")) {
            return ResponseEntity.badRequest().body("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!");
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

    // ==========================================
    // API LẤY DANH SÁCH TOÀN BỘ NGƯỜI DÙNG (DÀNH CHO ADMIN)
    // ==========================================
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserEntity> entities = userRepository.findAllUsers();
        List<UserDTO> dtos = new ArrayList<>();
        
        for (UserEntity entity : entities) {
            UserDTO dto = new UserDTO();
            dto.setUserId(entity.getUser_id());
            dto.setFullName(entity.getFullname());
            dto.setEmail(entity.getEmail());
            dto.setPhoneNumber(entity.getPhone());
            dto.setRoleId(entity.getRole_id());
            dto.setIsActive(entity.getIs_active());
            dtos.add(dto);
        }
        
        return ResponseEntity.ok(dtos);
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
        dto.setIsActive(entity.getIs_active());
        
        return ResponseEntity.ok(dto);
    }

    // Cập nhật thông tin người dùng
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUserProfile(@PathVariable Integer id, @RequestBody UserDTO userDTO) {
        UserEntity existingUser = userRepository.findById(id);
        if (existingUser == null) {
            return ResponseEntity.status(404).body("Không tìm thấy người dùng!");
        }

        if (userDTO.getFullName() == null || userDTO.getFullName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Họ và tên không được để trống!");
        }
        if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email không được để trống!");
        }
        if (userDTO.getPhoneNumber() == null || userDTO.getPhoneNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Số điện thoại không được để trống!");
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(existingUser.getEmail()) && userRepository.checkEmailExists(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email này đã được sử dụng bởi tài khoản khác!");
        }
        if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().equals(existingUser.getPhone()) && userRepository.checkPhoneExists(userDTO.getPhoneNumber())) {
            return ResponseEntity.badRequest().body("Số điện thoại này đã được sử dụng bởi tài khoản khác!");
        }

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
        
        if (oldPassword == null || oldPassword.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Mật khẩu không được để trống!");
        }
        
        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")) {
            return ResponseEntity.badRequest().body("Mật khẩu mới phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!");
        }

        boolean isSuccess = userRepository.changePassword(id, oldPassword, newPassword);
        if (isSuccess) {
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        }
        return ResponseEntity.badRequest().body("Mật khẩu hiện tại không chính xác!");
    }

    // ==========================================
    // API KHÓA / MỞ KHÓA TÀI KHOẢN KHÁCH HÀNG
    // ==========================================
    @PutMapping("/{id}/status")
    public ResponseEntity<String> toggleUserStatus(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        Integer newStatus = null;
        // Linh hoạt chấp nhận cả "isActive" và "is_active" từ Frontend
        if (payload.containsKey("isActive")) {
            newStatus = Integer.valueOf(payload.get("isActive").toString());
        } else if (payload.containsKey("is_active")) {
            newStatus = Integer.valueOf(payload.get("is_active").toString());
        }

        if (newStatus == null) return ResponseEntity.badRequest().body("Thiếu dữ liệu trạng thái!");
        
        boolean isSuccess = userRepository.updateUserStatus(id, newStatus);
        if (isSuccess) {
            return ResponseEntity.ok("Cập nhật trạng thái thành công!");
        }
        return ResponseEntity.badRequest().body("Cập nhật thất bại!");
    }
}