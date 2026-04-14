package com.javaweb.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaweb.repository.BrandRepository;
import com.javaweb.service.BrandService;
import model.ItemDTO;
import com.javaweb.repository.entity.BrandEntity;
import java.util.ArrayList;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandRepository brandRepository;
    @Override
    public List<ItemDTO> findAll()
    {
        List<BrandEntity> entities = brandRepository.findAll();
        List<ItemDTO> result = new ArrayList<>();
        
        for(BrandEntity item : entities)
        {
            ItemDTO dto = new ItemDTO();
            dto.setId(item.getBrand_id());
            dto.setName(item.getName());
            result.add(dto);
        }
        return result;
    }
}