package com.example.demo.Repository;

import com.example.demo.Model.Order;
import com.example.demo.Model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailsRepo extends JpaRepository<OrderDetails,Long> {
    @Query("select o from Order o where o.user.id =?1")
    List<Order> findAllByUserId(int id);
}
