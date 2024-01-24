package com.project.storage.storage.service;

import com.project.storage.storage.entity.Material;

import java.util.List;

public interface MaterialService {

    Material save(Material material);

    Material findById(int id);

    List<Material> findAll();

    void edit (int id , Material material ) ;


}
