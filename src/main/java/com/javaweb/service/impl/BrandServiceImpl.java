package com.javaweb.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaweb.repository.BrandRepository;
import com.javaweb.service.BrandService;
import model.ItemDTO;

@Service
public class BrandServiceImpl implements BrandService {
    
    @Autowired
    private BrandRepository brandRepository;

    @Override
    public List<ItemDTO> findAll() {
        return brandRepository.findAll();
    }
}