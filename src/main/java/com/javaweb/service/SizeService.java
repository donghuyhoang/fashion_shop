package com.javaweb.service;

import model.ItemDTO;
import java.util.List;

public interface SizeService {
    List<ItemDTO> findAll();
}