package com.project.storage.storage.service;

import com.project.storage.storage.entity.MsizeEntity;
import com.project.storage.storage.repository.MsizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MsizeServiceImpl implements MsizeService {

    @Autowired
    private MsizeRepository msizeRepository;


    public MsizeServiceImpl(MsizeRepository msizeRepository) {
        this.msizeRepository = msizeRepository;
    }


    @Override
    public MsizeEntity findById(int id) {
        return msizeRepository.findById(id);
    }

    @Override
    public List<MsizeEntity> findAll() {
        return msizeRepository.findAll();
    }
}
