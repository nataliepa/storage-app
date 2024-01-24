package com.project.storage.storage.service;



import com.project.storage.storage.entity.Role;

import java.util.List;

public interface UserRoleService {

    Role findById(int id);

    List<Role> findAll();

    Role findByName (String name);

}
