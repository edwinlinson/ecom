package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Data
public class Wallet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private Long id;

    @OneToOne
    private User user;

    private double balance;
}
