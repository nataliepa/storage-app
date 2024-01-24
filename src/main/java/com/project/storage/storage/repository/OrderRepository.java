package com.project.storage.storage.repository;

import com.project.storage.storage.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer> {

    Orders findById(int id);

    List<Orders> findAll();


}