package com.javaweb.repository;
import java.util.List;
import com.javaweb.repository.entity.ColorEntity;

public interface ColorRepository
{
    List<ColorEntity> findAll();
}
