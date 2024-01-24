package com.project.storage.storage.repository;


import com.project.storage.storage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
//    @Query(value = " SELECT * FROM user WHERE username = 'admin'",nativeQuery = true)
    User findByUsername(String username);
    User findById(int id);
    List<User> findAll();


}
