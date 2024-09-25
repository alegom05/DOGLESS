package com.example.holamundo.repository2;

import com.example.holamundo.entity2.OrderDetailId;
import com.example.holamundo.entity2.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, OrderDetailId> {
}

