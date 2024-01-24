package com.project.storage.storage.service;


import com.project.storage.storage.entity.Store;
import com.project.storage.storage.repository.StroreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StroreRepository stroreRepository;


    public StoreServiceImpl(StroreRepository stroreRepository) {
        this.stroreRepository = stroreRepository;
    }


    @Override
    public Store save(Store store) {
        return stroreRepository.save(store);
    }

    @Override
    public Store findById(int id) {
      return   stroreRepository.findById(id);
    }

    @Override
    public List<Store> findAll() {
        return stroreRepository.findAll();
    }

    @Override
    public void edit(int id, Store store) {
        Store found = stroreRepository.findById(id);

        found.setTitle(store.getTitle());
        found.setAddress(store.getAddress());

        stroreRepository.save(found);
    }
}
