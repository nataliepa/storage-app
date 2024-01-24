package com.project.storage.storage.repository;


import com.project.storage.storage.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StroreRepository extends JpaRepository<Store, Integer> {

    Store findById(int id);

    List<Store> findAll();

}