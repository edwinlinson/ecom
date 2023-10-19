package com.example.demo.Controller;

import com.example.demo.Model.*;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.UserServiceImpl;
import com.example.demo.ServiceImpl.OrderService;
import com.example.demo.ServiceImpl.ShoppingCartServiceImpl;
import com.razorpay.Invoice;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;


@RestController
public class PaymentController {



//    @PostMapping("/payNow")
//    public String generateInvoice() throws RazorpayException {
//        RazorpayClient razorpay = new RazorpayClient("[YOUR_KEY_ID]", "[YOUR_KEY_SECRET]");
//
//        JSONObject invoiceRequest = new JSONObject();
//        invoiceRequest.put("type", "invoice");
//        invoiceRequest.put("description", "Invoice for the month of January 2020");
//        invoiceRequest.put("partial_payment",true);
//        JSONObject customer = new JSONObject();
//        customer.put("name","Gaurav Kumar");
//        customer.put("contact","9000090000");
//        customer.put("email","gaurav.kumar@example.com");
//        JSONObject billingAddress = new JSONObject();
//        billingAddress.put("line1","Ground & 1st Floor, SJR Cyber Laskar");
//        billingAddress.put("line2","Hosur Road");
//        billingAddress.put("zipcode","560068");
//        billingAddress.put("city","Bengaluru");
//        billingAddress.put("state","Karnataka");
//        billingAddress.put("country","in");
//        customer.put("billing_address",billingAddress);
//        JSONObject shippingAddress = new JSONObject();
//        shippingAddress.put("line1","Ground & 1st Floor, SJR Cyber Laskar");
//        shippingAddress.put("line2","Hosur Road");
//        shippingAddress.put("zipcode","560068");
//        shippingAddress.put("city","Bengaluru");
//        shippingAddress.put("state","Karnataka");
//        shippingAddress.put("country","in");
//        customer.put("shipping_address",shippingAddress);
//        invoiceRequest.put("customer",customer);
//        List<Object> lines = new ArrayList<>();
//        JSONObject lineItems = new JSONObject();
//        lineItems.put("name","Master Cloud Computing in 30 Days");
//        lineItems.put("description","Book by Ravena Ravenclaw");
//        lineItems.put("amount",399);
//        lineItems.put("currency","INR");
//        lineItems.put("quantity",1);
//        lines.add(lineItems);
//        invoiceRequest.put("line_items",lines);
//        invoiceRequest.put("email_notify", 1);
//        invoiceRequest.put("sms_notify", 1);
//        invoiceRequest.put("currency","INR");
//        invoiceRequest.put("expire_by", 1580479824);
//
//        try {
//            Invoice invoice = razorpay.invoices.create(invoiceRequest);
//        } catch (RazorpayException e) {
//            throw new RuntimeException(e);
//        }
//        return "work-done";
//    }
//    private RazorpayClient client;
//    private static final String SECRET_ID = "rzp_test_Evnp1zBkKHfvaS";
//    private static final String SECRET_KEY = "rjbVJRP6mfGYCR67oxAkLsUb";
//
//    @RequestMapping(path = "/payNow",method ={RequestMethod.GET,RequestMethod.POST})
//    public OrderResponse createOrder(@RequestBody OrderRequest request){
//        OrderResponse response = new OrderResponse();
//        try {
//            client = new RazorpayClient(SECRET_ID,SECRET_KEY);
//            Order order = createRazorPayOrder(request.getPrice());
//            System.out.println("---------------------------");
//            String orderId = (String) order.get("id");
//            System.out.println("Order ID: " + orderId);
//            System.out.println("---------------------------");
//            response.setRazorpayOrderId(orderId);
//            response.setApplicationFee("" + request.getPrice());
//            response.setSecretKey(SECRET_KEY);
//            response.setSecretId(SECRET_ID);
//            response.setPgName("razor1");
//            return response;
//        } catch (RazorpayException e){
//            e.printStackTrace();
//        }
//        return response;
//    }
//    private Order createRazorPayOrder(double amount) throws RazorpayException {
//        // Convert amount to paisa (smallest currency unit)
//        long amountInPaisa = (long) (amount * 100);
//
//        org.json.JSONObject options = new JSONObject();
//        options.put("amount", amountInPaisa);
//        options.put("currency", "INR");
//        options.put("receipt", "txn_123456");
//        options.put("payment_capture", 1); // You can enable this if you want to do Auto Capture.
//        return client.orders.create(options);
////    }
//    private RazorpayClient client;
//    @Autowired
//    OrderService orderService;
//    @Autowired
//    ProductService productService;
//    @Autowired
//    UserServiceImpl userService;
//    @Autowired
//    ShoppingCartServiceImpl cartService;
//
//    @PostMapping(value = "/checkout/confirmOrder")
//    public String confirmOrder(@RequestBody OrderRequest request, com.example.demo.Model.Order order, Model model, Principal principal, @RequestParam("paymentMethod") String paymentMethod) {
//        System.out.println("in checkout");
//        System.out.println("in checkout");
//        System.out.println("in checkout");
//        System.out.println("in checkout");
//        System.out.println("in checkout");
//        User user = userService.findUserByUsername(principal.getName());
//        ShoppingCart cart = user.getCart();
//        Set<CartItem> cartItems = cart.getCartItems();
//        if ("payNow".equals(paymentMethod)) {
//            try {
//                System.out.println("in if of paynow");
//                OrderRequest  orderRequest = new OrderRequest();
//                orderRequest.setName(user.getName());
//                orderRequest.setEmail(user.getEmail());
//                orderRequest.setPhoneNumber(user.getPhoneNumber());
//                orderRequest.setPrice(cart.getTotalPrice());
//
//                JSONObject orderJson = new JSONObject();
//                orderJson.put("amount", cart.getTotalPrice());
//                orderJson.put("currency", "INR");
//                orderJson.put("receipt", "order_receipt_" + System.currentTimeMillis());
//                orderJson.put("payment_capture", 1);
//                System.out.println(" " + orderJson.get("amount"));
//
//                com.razorpay.Order razOrder1 = client.orders.create(orderJson);
//                System.out.println(" "+razOrder1.get("id")+ " " +razOrder1.get("secret") );
//                if (razOrder1 !=null ){
//                    System.out.println("order != null");
//                    OrderResponse  orderResponse = new OrderResponse();
//                    orderResponse.setRazorpayOrderId(razOrder1.get("id"));
//                    orderResponse.setSecretKey(razOrder1.get("secret"));
//                    model.addAttribute("orderResponse", orderResponse);
//                } else {
//                    model.addAttribute("error", "Failed to create Razorpay Order");
//                    return "errorPage";
//                }
//            } catch (RazorpayException e) {
//                e.printStackTrace();
//                System.out.println("in catch");
//            }
//            return "OrderConfirmed";
////            return ResponseEntity.status(HttpStatus.OK).body(order.toString());
//
//        }
//        for (CartItem cartItem : cartItems){
//            Product product = cartItem.getProduct();
//            int orderQty = cartItem.getQuantity();
//            if (orderQty > product.getQty()){
//                model.addAttribute("error", "Product " + product.getName() + "is out of stock");
//                return "redirect:/cart";
//            }
//            product.setQty(product.getQty() - orderQty);
//            productService.addProduct(product);
//        }
//        order.setOrderDate(new Date());
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(order.getOrderDate());
//        calendar.add(Calendar.DAY_OF_MONTH,7);
//        order.setDeliveryDate(calendar.getTime());
//
//        order.setPaymentMethod(paymentMethod);
//        order.setOrderStatus("CONFIRMED");
//        order.setUser(user);
//        order.setTotalPrice(cart.getTotalPrice());
//
//        List<OrderDetails> orderDetails = new ArrayList<>();
//        for (CartItem cartItem :cartItems){
//            OrderDetails orderDetails1 = new OrderDetails();
//            orderDetails1.setOrder(order);
//            orderDetails1.setProduct(cartItem.getProduct());
//        }
//        order.setOrderDetails(orderDetails);
//
//        orderService.saveOrder(order);
//
//        cartService.clearCart(user.getEmail());
//
//        model.addAttribute("order", order);
//        return "OrderConfirmed";
//    }
}
