package com.javaweb.repository;
import java.util.List;

import com.javaweb.repository.entity.CategoryEntity;


public interface CategoryRepository {
    List<CategoryEntity> findAll();
}