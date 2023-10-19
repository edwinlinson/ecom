package com.example.demo.Repository;

import com.example.demo.Model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepo extends JpaRepository<ShoppingCart, Long> {

}
