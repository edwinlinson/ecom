package com.example.demo.ServiceImpl;

import com.example.demo.Model.*;
import com.example.demo.Repository.OrderRepo;
import com.example.demo.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Service
public class OrderService implements com.example.demo.Service.OrderService {
    @Autowired
    OrderRepo orderRepo;
    @Autowired
    ProductService productService;

    public void saveOrder(Order order){
        System.out.println("in save order method  "+order.getPaymentMethod());
        orderRepo.save(order);
    }

    @Override
    public List<Order> getOrderForCurrentUser(Principal principal) {
        if (principal == null ){
            throw new IllegalStateException("No user is logged in as of now");
        }
        String username = principal.getName();
        return orderRepo.findByUserEmail(username);
    }

//    @Override
//    public void confirmOrder(Order order, String paymentMethod, User user) {
//        try{
//            ShoppingCart cart = user.getCart();
//            Set<CartItem> cartItems = cart.getCartItems();
//            for (CartItem cartItem : cartItems){
//                Product product = cartItem.getProduct();
//                int orderQty = cartItem.getQuantity();
//                if (orderQty > product.getQty()){
//                    throw new StockOutException("Product " +product.getName() + " Is out of stock");
//                }
//
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    @Override
    public void cancel(Long orderId) {
        Optional<Order> order = orderRepo.findById(orderId);
        if (order.isPresent()){
            Order order1 =order.get();
            List<OrderDetails> orderDetails = order1.getOrderDetails();
            for (OrderDetails orderDetails1 :orderDetails){
                Product product = orderDetails1.getProduct();
                System.out.println(" "+product);
                int ordered = order1.getQuantity();
                int newqty = ordered + product.getQty();
                product.setQty(newqty);
                productService.addProduct(product);
            }
            order1.setOrderStatus("CANCELED");
            orderRepo.save(order1);
        } else {
            throw new RuntimeException("Order not found for ID :" +orderId);
        }

    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return orderRepo.findById(orderId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    @Override
    public void updateOrderStatus(Long orderId, String newStatus) {
        Optional<Order> optionalOrder =orderRepo.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setOrderStatus(newStatus);
            orderRepo.save(order);
        } else {
            throw new RuntimeException("Order not found for ID :" +orderId);
        }
    }

    public List<Order> getSalesData(Date fromDate, Date toDate) {
        List<Order> orders = orderRepo.findByOrderDateBetween(fromDate,toDate);
        orders.sort(Comparator.comparing(Order :: getOrderDate));
        return orders;
    }
}
