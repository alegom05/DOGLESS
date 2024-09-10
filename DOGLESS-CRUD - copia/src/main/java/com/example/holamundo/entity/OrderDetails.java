package com.example.holamundo.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table (name="`order details`")
public class OrderDetails {

        @EmbeddedId
        private OrderDetailId id;

        @MapsId("orderID")
        @ManyToOne
        @JoinColumn(name = "OrderID")
        private Order orderID;

        @MapsId("productID")
        @ManyToOne
        @JoinColumn(name = "ProductID")
        private Product productID;

        @Column(name = "UnitPrice", nullable = false, precision = 10, scale = 4)
        private BigDecimal unitPrice;

        @Column(name = "Quantity", nullable = false)
        private Integer quantity;

        @Column(name = "Discount", nullable = false)
        private Double discount;

        public OrderDetailId getId() {
            return id;
        }

        public void setId(OrderDetailId id) {
            this.id = id;
        }

        public Order getOrderID() {
            return orderID;
        }

        public void setOrderID(Order orderID) {
            this.orderID = orderID;
        }

        public Product getProductID() {
            return productID;
        }

        public void setProductID(Product productID) {
            this.productID = productID;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getDiscount() {
            return discount;
        }

        public void setDiscount(Double discount) {
            this.discount = discount;
        }

}
