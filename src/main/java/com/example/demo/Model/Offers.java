package com.example.demo.Model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Offers implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private double discount;

}
