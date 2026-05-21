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

// Phải import các class từ thư mục gốc vào
import com.javaweb.api.UserAPI;
import com.javaweb.repository.impl.UserRepositoryImpl;
import com.javaweb.repository.entity.UserEntity;
import com.javaweb.security.JwtUtils;
import model.UserDTO;

@WebMvcTest(UserAPI.class)
@AutoConfigureMockMvc(addFilters = false) 
public class UserAPITest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepositoryImpl userRepository; 

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

        userEntity = new UserEntity();
        userEntity.setUser_id(1);
        userEntity.setEmail("testapi@gmail.com");
        userEntity.setFullname("Test API User");
        userEntity.setRole_id(2);
        userEntity.setIs_active(1); 
    }

    @Test
    void testLoginAPI_Success() throws Exception {
        when(userRepository.findByEmailAndPassword(anyString(), anyString())).thenReturn(userEntity);
        when(jwtUtils.generateToken(anyString(), any(Integer.class), any(Integer.class))).thenReturn("mocked-jwt-token");

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