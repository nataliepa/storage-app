package com.project.storage.storage.service;


import com.project.storage.storage.entity.Material;
import com.project.storage.storage.entity.Orders;
import com.project.storage.storage.repository.MaterialRepository;
import com.project.storage.storage.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    private MaterialRepository materialRepository;


    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @Override
    public Orders save(Orders order) {

        return orderRepository.save(order);
    }

    @Override
    public Orders findById(int id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Orders> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public void deny(Orders order) {

        order.setStatus(1);  // απόρριψη παραγγελίας
        orderRepository.save(order);
    }

    @Override
    public void accept(Orders order) {
        order.setStatus(2); // εγκριση παραγγελίας
        orderRepository.save(order);

        Material mat = order.getMaterialByMaterialId();
       mat.setQuantity(mat.getQuantity() - order.getQuantity()); // αφαιρεση ζητουμενης ποσοτητας απο τα αποθεματα της κεντρικης αποθηκης
       materialRepository.save(mat);

    }


    @Override
    public void sold(Orders order) {
        orderRepository.save(order);
    }



}

