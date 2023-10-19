package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@Table(name="cart_items")
public class CartItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "shopping_cart_id", referencedColumnName = "shopping_cart_id")
    private ShoppingCart cart;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    private int quantity;
    private double unitPrice;

    @Override
    public int hashCode() {
//        return Objects.hash(id, product, quantity, unitPrice); // Exclude cart
        return Objects.hash(id,product,quantity,unitPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem cartItem)) return false;
        return id.equals(cartItem.id);
    }

}
