package com.javaweb.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.javaweb.service.impl.UserServiceImpl;
import com.javaweb.repository.UserRepository;
import com.javaweb.repository.entity.UserEntity;
import model.UserDTO;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

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
        when(userRepository.findByEmailAndPassword(anyString(), anyString())).thenReturn(testUserEntity);

        UserDTO result = userService.login("test@gmail.com", "Password@123");

        assertNotNull(result);
        assertEquals(1, result.getUserId());
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
        when(userRepository.register(any(UserEntity.class))).thenReturn(true);

        boolean result = userService.register(testUserDTO);

        assertTrue(result); 
        verify(userRepository, times(1)).register(any(UserEntity.class));
    }
}