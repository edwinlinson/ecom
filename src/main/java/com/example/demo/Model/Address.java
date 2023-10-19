package com.example.demo.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
public class Address implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private int id;
    @NotBlank(message = "Should not be blank")
    private String street;
    private String line1;
    private String line2;
    @NotBlank(message = "Should not be blank")
    private String state;
    @NotBlank(message = "Should not be blank")
    private String postalcode;
    @NotBlank(message = "Should not be blank")
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "user_id")
    private User user;

    @Column(name = "primary")
    private boolean primary;

    @OneToMany(mappedBy = "shippingAddress")
    private List<Order> orders;
}
