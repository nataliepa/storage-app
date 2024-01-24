package com.project.storage.storage.service;


import com.project.storage.storage.entity.Material;
import com.project.storage.storage.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialRepository materialRepository;


    public MaterialServiceImpl(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }


    @Override
    public Material save(Material material) {

        return materialRepository.save(material);
    }

    @Override
    public Material findById(int id) {
        return materialRepository.findById(id);
    }

    @Override
    public List<Material> findAll() {
        return materialRepository.findAll();
    }

    @Override
    public void edit(int id, Material material) {
        Material found = materialRepository.findById(id);

        found.setText(material.getText());

        if(material.getQuantity() == null){
            material.setQuantity(0);
        }
        found.setQuantity(material.getQuantity());
        found.setMsizeByMsize(material.getMsizeByMsize());
        materialRepository.save(found);
    }
}