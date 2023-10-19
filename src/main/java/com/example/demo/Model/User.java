package com.example.demo.Model;

import jakarta.persistence.*;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


@Entity
@Table(name="users1")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false,unique = true)
    @Email(message = "errors.invalid_email")
    private String email;

    @Column(nullable = false)
    @NotBlank
    private String password;

    @Column (nullable = false)
    @Size(min = 10,max = 10,message = "Please enter 10 digit valid phone number")
    private String phoneNumber;

    private String role;

    @Column (nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private ShoppingCart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @ManyToMany
    @JoinTable(name = "user_wishlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> wishlist;


    @Column(unique = true)
    private String referralCode;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id",referencedColumnName = "wallet_id")
    private Wallet wallet;

}
