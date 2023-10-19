package com.example.demo.Repository;

import com.example.demo.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByUserEmail(String username);

    List<Order> findByOrderDateBetween(Date fromDate, Date toDate);
}
