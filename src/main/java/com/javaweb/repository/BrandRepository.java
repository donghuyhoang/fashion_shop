package com.javaweb.repository;
import java.util.List;
import model.ItemDTO;

public interface BrandRepository {
    List<ItemDTO> findAll();
}