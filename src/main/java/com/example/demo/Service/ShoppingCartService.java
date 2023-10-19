package com.example.demo.Service;

import com.example.demo.Model.Product;
import com.example.demo.Model.ShoppingCart;

public interface ShoppingCartService {
    ShoppingCart addItemToCart(Product product, int qty, String username);

    ShoppingCart removeItemFromCart(Product product, String name);

    void clearCart(String username);
}
