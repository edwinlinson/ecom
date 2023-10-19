package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CollectionId;

import java.io.Serializable;

@Entity
@Data
@Table(name = "order_details")
public class OrderDetails implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_details_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @Column(name = "quantity1")
    private int quantity1;
}
