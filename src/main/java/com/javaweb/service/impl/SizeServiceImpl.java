package com.javaweb.service.impl;

import com.javaweb.repository.SizeRepository;
import com.javaweb.repository.entity.SizeEntity;
import com.javaweb.service.SizeService;
import model.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SizeServiceImpl implements SizeService {

    @Autowired

    private SizeRepository sizeRepository;

    @Override
    public List<ItemDTO> findAll() {
        List<SizeEntity> entities = sizeRepository.findAll();
        List<ItemDTO> result = new ArrayList<>();
        
        for (SizeEntity item : entities) {
            ItemDTO dto = new ItemDTO();
            dto.setId(item.getId()); 
            dto.setName(item.getName());
            result.add(dto);
        }
        return result;
    }
}