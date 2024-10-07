package com.coupons_management.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Product product;
    private int quantity;
    private double totalDiscount; // discount applied on this product

    // Getter for product
    public Product getProduct() {
        return product;
    }

    // Getter for quantity
    public int getQuantity() {
        return quantity;
    }

    // Getter for total discount applied on the item
    public double getTotalDiscount() {
        return totalDiscount;
    }
}
