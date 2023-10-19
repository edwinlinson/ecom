package com.example.demo.Model;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderRequest implements Serializable {
    String name;
    String email;
    String phoneNumber;
    Double price;
    String paymentMethod;
}
