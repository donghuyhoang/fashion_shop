package com.javaweb.api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.javaweb.service.UserService;
import model.UserDTO;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserAPI {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO loginDTO)
    {
        UserDTO user = userService.login(loginDTO.getEmail(), loginDTO.getPassword());

        if(user != null)
        {
            return ResponseEntity.ok(user);
        }
        else
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email hoặc mật khẩu không đúng");
        }
    }
}
