package com.javaweb.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper; 

import com.javaweb.service.impl.UserServiceImpl;
import com.javaweb.repository.UserRepository;
import com.javaweb.repository.entity.UserEntity;
import model.UserDTO;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper; 

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO testUserDTO;
    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        testUserDTO = new UserDTO();
        testUserDTO.setEmail("test@gmail.com");
        testUserDTO.setPassword("Password@123");
        testUserDTO.setFullName("Nguyen Van A");
        testUserDTO.setPhoneNumber("0123456789");

        testUserEntity = new UserEntity();
        testUserEntity.setUser_id(1);
        testUserEntity.setEmail("test@gmail.com");
        testUserEntity.setFullname("Nguyen Van A");
        testUserEntity.setRole_id(2);
    }

    @Test
    void testLogin_Success() {
        // Set id cho DTO giả lập để tránh lỗi lệch Assertion (assertEquals(1, null))
        testUserDTO.setUserId(1);

        when(userRepository.findByEmailAndPassword(anyString(), anyString())).thenReturn(testUserEntity);
        // GIẢ LẬP MODEL MAPPER ÉP KIỂU TỪ ENTITY SANG DTO
        when(modelMapper.map(any(), eq(UserDTO.class))).thenReturn(testUserDTO);

        UserDTO result = userService.login("test@gmail.com", "Password@123");

        assertNotNull(result);
        assertEquals(1, result.getUserId()); // Giờ test sẽ pass vì userId đã được gán bằng 1
        assertEquals("test@gmail.com", result.getEmail());
        verify(userRepository, times(1)).findByEmailAndPassword("test@gmail.com", "Password@123");
    }

    @Test
    void testLogin_Fail_WrongCredentials() {
        when(userRepository.findByEmailAndPassword(anyString(), anyString())).thenReturn(null);
        UserDTO result = userService.login("test@gmail.com", "WrongPass");
        assertNull(result); 
    }

    @Test
    void testRegister_Fail_EmailExists() {
        when(userRepository.checkEmailExists(anyString())).thenReturn(true);
        boolean result = userService.register(testUserDTO);

        assertFalse(result); 
        verify(userRepository, never()).register(any(UserEntity.class)); 
    }

    @Test
    void testRegister_Success() {
        when(userRepository.checkEmailExists(anyString())).thenReturn(false);
        // GIẢ LẬP MODEL MAPPER ÉP KIỂU TỪ DTO SANG ENTITY
        when(modelMapper.map(any(), eq(UserEntity.class))).thenReturn(testUserEntity);
        when(userRepository.register(any(UserEntity.class))).thenReturn(true);

        boolean result = userService.register(testUserDTO);

        assertTrue(result); 
        verify(userRepository, times(1)).register(any(UserEntity.class));
    }
}