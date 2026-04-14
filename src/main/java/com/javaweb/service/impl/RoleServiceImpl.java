package com.javaweb.service.impl;

import com.javaweb.repository.RoleRepository;
import com.javaweb.repository.entity.RoleEntity;
import com.javaweb.service.RoleService;
import model.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<ItemDTO> findAll() {
        List<RoleEntity> entities = roleRepository.findAll();
        List<ItemDTO> result = new ArrayList<>();
        for (RoleEntity item : entities) {
            ItemDTO dto = new ItemDTO();
            dto.setId(item.getRole_id());
            dto.setName(item.getRole_name());
            result.add(dto);
        }
        return result;
    }
}