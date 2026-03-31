package com.javaweb.repository;
import java.util.List;
import model.ItemDTO;

public interface CategoryRepository {
    List<ItemDTO> findAll();
}