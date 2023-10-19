package com.example.demo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
public class Coupon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String code;
    private double discount;
    private double minimum;
    private boolean active;

    public void toggleActive() {
        this.active = !this.active;
    }
}
