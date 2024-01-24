package com.project.storage.storage.repository;

import com.project.storage.storage.entity.MsizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MsizeRepository extends JpaRepository<MsizeEntity, Integer> {

    MsizeEntity findById(int id);

    List<MsizeEntity> findAll();

}