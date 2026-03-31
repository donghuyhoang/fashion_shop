package com.javaweb.service;
import java.util.List;
import model.ItemDTO;

public interface BrandService {
    List<ItemDTO> findAll();
}