package com.example.demo.Controller;

import com.example.demo.Model.*;
import com.example.demo.Repository.OrderRepo;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.UserServiceImpl;
import com.example.demo.ServiceImpl.AddressImpl;
import com.example.demo.ServiceImpl.OrderService;
import com.example.demo.ServiceImpl.ShoppingCartServiceImpl;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.razorpay.RazorpayClient.*;

import java.security.Principal;
import java.util.*;

@Controller
public class ShoppingCartController{
    @Autowired
    private ProductService productService;
    @Autowired
    private ShoppingCartServiceImpl cartService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private AddressImpl addressService;
    @Autowired
    private OrderService orderService;
    @Autowired
    RazorpayClient client;
    @Autowired
    OrderRepo orderRepo;

    @GetMapping("/cart")
    public String getCart(Model model, Principal principal){
        if (principal == null){
            return "redirect:/login";
        }
        model.addAttribute("name",principal.getName());
        User user = userService.findUserByUsername(principal.getName());

        ShoppingCart shoppingCart = user.getCart();
        double total = shoppingCart.getTotalPrice();
        int totalQuantity = shoppingCart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        model.addAttribute("cartItems",cartItems);
        model.addAttribute("total",total);
        model.addAttribute("totalqty",totalQuantity);
        return "cart";
    }
    @GetMapping("/checkout2")
    public String checkout2(Model model,
                            Principal principal){
        User user= userService.findUserByUsername(principal.getName());
        List<Address> addresses =addressService.getAllAddressesByUser(user);
        ShoppingCart cart = user.getCart();
        double totalprice= cart.getTotalPrice();
        int totalQuantity = cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        Set<CartItem> cartItems = cart.getCartItems();
        model.addAttribute("cartItems",cartItems);
        model.addAttribute("user", user);
        model.addAttribute("addresses",addresses);
        model.addAttribute("totalprice", totalprice);
        return "checkout1";
    }
    @GetMapping("/addToCart/{id}")
    public String addToCart(@PathVariable("id") Long id, @RequestParam(value = "qty", required = false, defaultValue = "1") int qty,
                            HttpServletRequest request, Model model, Principal principal, HttpSession session, RedirectAttributes attributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        Product product = productService.getProductById(id).get();

        if (qty > product.getQty()) {
            attributes.addFlashAttribute("error", "Ordered quantity exceeds quantity in stock");
            return "redirect:/shop/viewproduct/{id}";
        }

        User user = userService.findUserByUsername(principal.getName());
        ShoppingCart cart = user.getCart();
        if (cart == null){
            cart = new ShoppingCart();
            cart.setUser(user);
        }
        cartService.addItemToCart(product,qty, principal.getName());
        model.addAttribute("success","added to cart successfully");
        return "redirect:/shop";
    }

    @PostMapping("/cart/removeItem/{id}")
    public String removeFromCart(@PathVariable("id") Long id,
                                 HttpServletRequest request,
                                 Model model, Principal principal,
                                 HttpSession session){
        if(principal == null){
            return "redirect:/login";
        }
        Product product = productService.getProductById(id).orElse(null);
        if (product == null ){
            return "redirect:/cart";
        }
        cartService.removeItemFromCart(product , principal.getName());
        model.addAttribute("success", "Item Removed Succesfully");
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model,
                           Principal principal){
        User user= userService.findUserByUsername(principal.getName());
        List<Address> addresses =addressService.getAllAddressesByUser(user);
        ShoppingCart cart = user.getCart();
        double totalprice= cart.getTotalPrice();
        model.addAttribute("user", user);
        model.addAttribute("addresses",addresses);
        model.addAttribute("totalprice", totalprice);
        model.addAttribute("paymentMethod", "CashOnDelivery");
        return "checkout";
    }


    @PostMapping("/checkout/addAddress")
    public String addAddress(Address address, Principal principal){
        User user =userService.findUserByUsername(principal.getName());
        address.setUser(user);
        addressService.addAddress(address);
        return "redirect:/checkout";
    }
    @PostMapping(value = "/checkout/confirmOrder")
    public String confirmOrder(Order order,Model model,
                               Principal principal, @RequestParam("paymentMethod") String paymentMethod,@RequestParam("selectedAddressId") int selectedAddressId, RedirectAttributes attributes) {
        System.out.println("in checkout");
        System.out.println("Selected payment method: " + paymentMethod);
        try {
            User user = userService.findUserByUsername(principal.getName());
            Address selected = addressService.getAddressById(selectedAddressId).get();
            ShoppingCart cart = user.getCart();
            Set <CartItem> cartItems = cart.getCartItems();
            for (CartItem cartItem : cartItems){
                Product product = cartItem.getProduct();
                int orderQty = cartItem.getQuantity();
                if (orderQty > product.getQty()){
                    attributes.addFlashAttribute("error", "Product " +product.getName() + "is out of stock");
                    model.addAttribute("error", "Product " + product.getName() + "is out of stock");
                    return "redirect:/cart";
                }
                product.setQty(product.getQty() - orderQty);
                productService.addProduct(product);
            }
            order.setOrderDate(new Date());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(order.getOrderDate());
            calendar.add(Calendar.DAY_OF_MONTH,7);
            order.setDeliveryDate(calendar.getTime());

//            order.setPaymentMethod(paymentMethod);
            if ("payNow".equals(paymentMethod)){
                order.setPaymentMethod("PAID");
            }else {
                order.setPaymentMethod(paymentMethod);
            }
            order.setOrderStatus("CONFIRMED");
            order.setUser(user);
            order.setTotalPrice(cart.getTotalPrice());

            List<OrderDetails> orderDetails = new ArrayList<>();
            for (CartItem cartItem :cartItems){
                OrderDetails orderDetails1 = new OrderDetails();
                orderDetails1.setOrder(order);
                orderDetails1.setProduct(cartItem.getProduct());
                orderDetails1.setQuantity1(cartItem.getQuantity());
                orderDetails.add(orderDetails1);
                System.out.println(" order details. product " +orderDetails1.getProduct());
                System.out.println(" "+cartItem.getProduct()+" in order details for loop");
            }
            cartService.clearCart(user.getEmail());
            order.setOrderDetails(orderDetails);
            order.setShippingAddress(selected);
            orderRepo.save(order);
            model.addAttribute("order", order);

        } catch (Exception e){
            e.printStackTrace();
        }
        return "OrderConfirmed";
    }
//    @PostMapping(value = "/checkout/confirmOrder")
//    public String confirmOrder(Order order,Model model, Principal principal, @RequestParam("paymentMethod") String paymentMethod) {
//        System.out.println("in checkout");
//        try {
//            User user = userService.findUserByUsername(principal.getName());
//            ShoppingCart cart = user.getCart();
//            Set <CartItem> cartItems = cart.getCartItems();
//            for (CartItem cartItem : cartItems){
//                Product product = cartItem.getProduct();
//                int orderQty = cartItem.getQuantity();
//                if (orderQty > product.getQty()){
//                    model.addAttribute("error", "Product " + product.getName() + "is out of stock");
//                    return "redirect:/cart";
//                }
//                product.setQty(product.getQty() - orderQty);
//                productService.addProduct(product);
//            }
//            order.setOrderDate(new Date());
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(order.getOrderDate());
//            calendar.add(Calendar.DAY_OF_MONTH,7);
//            order.setDeliveryDate(calendar.getTime());
//
//            order.setPaymentMethod(paymentMethod);
//            order.setOrderStatus("CONFIRMED");
//            order.setUser(user);
//            order.setTotalPrice(cart.getTotalPrice());
//
//            List<OrderDetails> orderDetails = new ArrayList<>();
//            for (CartItem cartItem :cartItems){
//                OrderDetails orderDetails1 = new OrderDetails();
//                orderDetails1.setOrder(order);
//                orderDetails1.setProduct(cartItem.getProduct());
//                orderDetails1.setQuantity1(cartItem.getQuantity());
//                orderDetails.add(orderDetails1);
//                System.out.println(" order details. product " +orderDetails1.getProduct());
//                System.out.println(" "+cartItem.getProduct()+" in order details for loop");
//            }
//            order.setOrderDetails(orderDetails);
//
//            orderService.saveOrder(order);
//
//            cartService.clearCart(user.getEmail());
//
//            model.addAttribute("order", order);
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return "OrderConfirmed";
//    }
//
//    @PostMapping(value = "/checkout/confirmOrder")
//    public String confirmOrder(@RequestBody OrderRequest request, Order order,Model model, Principal principal, @RequestParam("paymentMethod") String paymentMethod) {
//        System.out.println("in checkout");
//        User user = userService.findUserByUsername(principal.getName());
//        ShoppingCart cart = user.getCart();
//        Set <CartItem> cartItems = cart.getCartItems();
//        if ("payNow".equals(paymentMethod)) {
//            return "redirect:/payNow";
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
//       orderService.saveOrder(order);
//
//        cartService.clearCart(user.getEmail());
//
//        model.addAttribute("order", order);
//        return "OrderConfirmed";
//    }

//    @RequestMapping(value = "/checkout/confirmOrder", method = RequestMethod.POST)
//    @ResponseBody
//    public String createOrder(@RequestBody Map<String,Object> data,Order order,Model model, Principal principal){
//        String paymentMethod = data.get("paymentMethod").toString();
//        Double totalprice = (double) data.get("totalprice");
//        User user = userService.findUserByUsername(principal.getName());
//        ShoppingCart cart = user.getCart();
//        Set <CartItem> cartItems = cart.getCartItems();
//        if (paymentMethod.equals("cod")){
//            for (CartItem cartItem : cartItems){
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
//        order.setPaymentMethod("Cash On Delivery");
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
//       orderService.saveOrder(order);
//
//        cartService.clearCart(user.getEmail());
//
//        model.addAttribute("order", order);
//        return "OrderConfirmed";
//        }
//
//
//    }

    @GetMapping("access-denied")
    public String getDenied(){
     return "access-denied";
    }

    @RequestMapping(value = "/payNow",method = RequestMethod.POST)
    @ResponseBody
    public String payNow(@RequestBody Map<String,Object> data) throws RazorpayException {
        System.out.println("In pay now backend");
        System.out.println(data);
        int amt = Integer.parseInt(data.get("amount").toString());
        var client =  new RazorpayClient("rzp_test_Evnp1zBkKHfvaS","rjbVJRP6mfGYCR67oxAkLsUb");
        JSONObject ob = new JSONObject();
        ob.put("amount",amt*100);
        ob.put("currency","INR");
        ob.put("receipt","txn_235425");

        com.razorpay.Order order = client.orders.create(ob);
        System.out.println(order);
        return order.toString();
    }
}
