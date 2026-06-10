package com.javaweb.api;

import com.javaweb.service.CategoryService;
import model.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/categories")
public class CategoryAPI {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public List<ItemDTO> getCategories() {
        return categoryService.findAll();
    }
}