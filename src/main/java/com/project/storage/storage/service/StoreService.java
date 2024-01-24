package com.project.storage.storage.service;

import com.project.storage.storage.entity.Store;

import java.util.List;

public interface StoreService {

    Store save(Store store);


    Store findById(int id);

    List<Store> findAll();

    void edit (int id , Store store ) ;
}
