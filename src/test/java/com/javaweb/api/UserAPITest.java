package com.javaweb.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.repository.impl.UserRepositoryImpl; // Import UserRepositoryImpl thay vì UserService
import com.javaweb.repository.entity.UserEntity;
import com.javaweb.security.JwtUtils;
import model.UserDTO;

@WebMvcTest(UserAPI.class)
@AutoConfigureMockMvc(addFilters = false) 
public class UserAPITest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepositoryImpl userRepository; // Đổi sang mock UserRepositoryImpl giống như Controller Autowired

    @MockBean
    private JwtUtils jwtUtils; 

    @Autowired
    private ObjectMapper objectMapper; 

    private UserDTO userDTO;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setEmail("testapi@gmail.com");
        userDTO.setPassword("StrongPass@123");
        userDTO.setFullName("Test API User");
        userDTO.setPhoneNumber("0987654321");

        // Sử dụng bộ mã hóa để tạo chuỗi BCrypt chuẩn cho mật khẩu "StrongPass@123"
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        userEntity = new UserEntity();
        userEntity.setUser_id(1);
        userEntity.setEmail("testapi@gmail.com");
        userEntity.setFullname("Test API User"); // Khớp đúng setFullname() trong UserEntity của bạn
        userEntity.setPhone("0987654321");       // Khớp đúng setPhone() trong UserEntity của bạn
        userEntity.setPassword_hash(encoder.encode("StrongPass@123")); // Đồng bộ hash pass
        userEntity.setRole_id(2);
        userEntity.setIs_active(1);
    }

    @Test
    void testLoginAPI_Success() throws Exception {
        // 1. Giả lập tìm thấy UserEntity có password_hash khớp với mật mã thô sau khi giải mã
        when(userRepository.findByEmail("testapi@gmail.com")).thenReturn(userEntity);
        
        // 2. Giả lập sinh mã Token thành công
        when(jwtUtils.generateToken("testapi@gmail.com", 2, 1)).thenReturn("mocked-jwt-token");

        // 3. Thực thi gọi API
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token")) 
                .andExpect(jsonPath("$.email").value("testapi@gmail.com"));
    }

    @Test
    void testLoginAPI_Fail_MissingFields() throws Exception {
        userDTO.setEmail(""); 

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Vui lòng nhập đầy đủ email và mật khẩu!"));
    }

    @Test
    void testRegisterAPI_Success() throws Exception {
        when(userRepository.checkEmailExists(anyString())).thenReturn(false);
        when(userRepository.checkPhoneExists(anyString())).thenReturn(false);
        when(userRepository.register(any(UserEntity.class))).thenReturn(true);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Đăng ký thành công!"));
    }

    @Test
    void testRegisterAPI_Fail_WeakPassword() throws Exception {
        userDTO.setPassword("123"); 

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!"));
    }
}