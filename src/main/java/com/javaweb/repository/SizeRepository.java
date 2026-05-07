package com.javaweb.repository;

import com.javaweb.repository.entity.SizeEntity;
import java.util.List;

public interface SizeRepository {
    List<SizeEntity> findAll();
}