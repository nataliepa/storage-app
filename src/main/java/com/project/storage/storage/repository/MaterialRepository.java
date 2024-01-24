package com.project.storage.storage.repository;

import com.project.storage.storage.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {

    Material findById(int id);

    List<Material> findAll();


}