package com.javaweb.api;

import com.javaweb.service.RoleService;
import model.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/roles")
public class RoleAPI {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<ItemDTO> getRoles() {
        return roleService.findAll();
    }
}