package com.project.storage.storage.service;

import com.project.storage.storage.entity.Orders;

import java.util.List;


public interface OrderService {

    Orders save(Orders order);


    Orders findById(int id);

    List<Orders> findAll();

    void deny(Orders order);

    void accept(Orders order);


    void sold (Orders order);

}
