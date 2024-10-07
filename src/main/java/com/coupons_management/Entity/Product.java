package com.coupons_management.Entity;

import lombok.Data;

@Data
public class Product {
    private long productId;
    private double price;

    // Getter for productId
    public long getProductId() {
        return productId;
    }

    // Getter for price
    public double getPrice() {
        return price;
    }
}
