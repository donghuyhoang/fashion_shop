package com.javaweb.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaweb.repository.CategoryRepository;
import com.javaweb.service.CategoryService;

import model.ItemDTO;
import com.javaweb.repository.entity.CategoryEntity;
import java.util.ArrayList;
@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<ItemDTO> findAll() {
        List<CategoryEntity> entities = categoryRepository.findAll();
        List<ItemDTO> result = new ArrayList<>();

        for(CategoryEntity item : entities)
        {
            ItemDTO dto = new ItemDTO();
            dto.setId(item.getCategory_id());
            dto.setName(item.getName());
            result.add(dto);
        }
        return result;
    }
}
