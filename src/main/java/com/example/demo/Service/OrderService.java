package com.example.demo.Service;

import com.example.demo.Model.Order;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    public void saveOrder(Order order);
    public List<Order> getOrderForCurrentUser(Principal principal);

//    public void confirmOrder(Order order, String paymentMethod, User user);
    void cancel(Long orderId);

    Optional<Order> findOrderById(Long orderId);
    public List<Order> getAllOrders();
    void updateOrderStatus(Long orderId, String newStatus);
}
