package com.example.holamundo.repository;

import com.example.holamundo.entity.OrderDetailId;
import com.example.holamundo.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, OrderDetailId> {
}

