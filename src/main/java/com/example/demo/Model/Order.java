package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@ToString
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;


    private Date orderDate;
    private Date deliveryDate;
    private String orderStatus;
    private double totalPrice;
    private int quantity;
    private String paymentMethod;
    private boolean isAccept;

    @ManyToOne( fetch = FetchType.EAGER)
    @JoinColumn( name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany( mappedBy = "order" ,cascade = CascadeType.ALL)
    private List<OrderDetails> orderDetails;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address shippingAddress;


//    public String paymentId;
}
