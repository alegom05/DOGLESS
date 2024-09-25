package com.example.holamundo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class OrderDetailId implements Serializable {

    @Column(name = "OrderID", nullable = false)
    private Integer orderID;

    @Column(name = "ProductID", nullable = false)
    private Integer productID;

    public Integer getOrderID() {
        return orderID;
    }
    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }
    public Integer getProductID() {
        return productID;
    }
    public void setProductID(Integer productID) {
        this.productID = productID;
    }

}
