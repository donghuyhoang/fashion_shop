package com.javaweb.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javaweb.repository.ColorRepository;
import com.javaweb.repository.entity.ColorEntity;
import com.javaweb.service.ColorService;
import model.ItemDTO;

@Service
public class ColorServiceImpl implements ColorService {

    @Autowired
    private ColorRepository colorRepository; // Đơn giản và gọn gàng thế này thôi!

    @Override
    public List<ItemDTO> findAll() {
        List<ColorEntity> entities = colorRepository.findAll();
        List<ItemDTO> result = new ArrayList<>();

        for (ColorEntity colorEntity : entities) {
            ItemDTO itemDTO = new ItemDTO();
            itemDTO.setId(colorEntity.getColor_id()); // Chú ý: Đảm bảo trong ColorEntity của bạn có hàm getColor_id()
            itemDTO.setName(colorEntity.getName());
            result.add(itemDTO);
        }
        return result;
    }
}