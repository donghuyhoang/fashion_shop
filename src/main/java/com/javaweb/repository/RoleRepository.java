package com.javaweb.repository;
import java.util.List;
import com.javaweb.repository.entity.RoleEntity;

public interface RoleRepository 
{
    List<RoleEntity> findAll();
    RoleEntity findById(Integer id);
}
