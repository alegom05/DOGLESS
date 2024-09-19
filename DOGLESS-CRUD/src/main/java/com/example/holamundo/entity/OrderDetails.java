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
        @JoinColumn(name = "orderid")
        private Order orderID;

        @MapsId("productID")
        @ManyToOne
        @JoinColumn(name = "productid")
        private Product productID;

        @Column(name = "unitprice", nullable = false, precision = 10, scale = 4)
        private BigDecimal unitPrice;

        @Column(name = "quantity", nullable = false)
        private Integer quantity;

        @Column(name = "discount", nullable = false)
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
