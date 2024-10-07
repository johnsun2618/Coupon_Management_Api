package com.coupons_management.Entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class Cart {
    private List<CartItem> cartItems; // renamed from 'items' to 'cartItems'
    private double totalPrice;

    // Method to get total amount from all cart items
    public double getTotalAmount() {
        return items.stream()
                .mapToDouble(cartItem -> cartItem
                        .getProduct()
                        .getPrice() * cartItem
                        .getQuantity() - cartItem
                        .getTotalDiscount())
                .sum();
    }

    private List<CartItem> items;

}
