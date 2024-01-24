package com.project.storage.storage.service;

import com.project.storage.storage.entity.MsizeEntity;

import java.util.List;


public interface MsizeService {

    MsizeEntity findById(int id);

    List<MsizeEntity> findAll();
}
