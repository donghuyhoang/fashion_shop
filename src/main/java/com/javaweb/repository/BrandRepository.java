package com.javaweb.repository;
import java.util.List;

import com.javaweb.repository.entity.BrandEntity;


public interface BrandRepository {
    List<BrandEntity> findAll();
}