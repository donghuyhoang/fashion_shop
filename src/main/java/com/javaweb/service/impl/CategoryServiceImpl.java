package com.javaweb.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaweb.repository.CategoryRepository;
import com.javaweb.service.CategoryService;

import model.ItemDTO;
@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<ItemDTO> findAll() {
        return categoryRepository.findAll();
    }
}
