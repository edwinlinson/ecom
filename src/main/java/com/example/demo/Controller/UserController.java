package com.example.demo.Controller;

import com.example.demo.Model.*;
import com.example.demo.Repository.UserRepo;
import com.example.demo.Service.UserServiceImpl;
import com.example.demo.ServiceImpl.AddressImpl;
import com.example.demo.ServiceImpl.OrderService;
import com.example.demo.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.model.IModel;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Controller
public class UserController {
    @Autowired
    OrderService orderService;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    AddressImpl addressService;
    @Autowired
    UserRepo userRepo;

    @GetMapping("/user")
    public String userDash(@ModelAttribute("user")User user, Model model){
        String name = user.getName();
        model.addAttribute("name",name);
        return "userDashboard";
    }
    @GetMapping("/orders")
    public String getOrders(Model model, Principal principal){
        if (principal != null){
            List<Order> orderList =orderService.getOrderForCurrentUser(principal);
            model.addAttribute("orderList",orderList);
        }
        return "OrderManage";
    }
    @PostMapping("/orders/cancel/{orderId}")
    public String cancelOrder(@PathVariable Long orderId){
        Optional<Order> order = orderService.findOrderById(orderId);
        if (order.isPresent()) {
            System.out.println("order is present");
            orderService.cancel(orderId);
        } else {
            System.out.println("order is not present");
        }
        return "redirect:/orders";
    }
    @GetMapping("/address")
    public String getAddress(Model model,Principal principal){
        User user = userService.findUserByUsername(principal.getName());
        List<Address> addresses = user.getAddresses();
        model.addAttribute("addresses",addresses);
        return "AddressManage";
    }
    @GetMapping("/addresses/delete/{addressId}")
    public String deleteAddress(@PathVariable int addressId) {
        addressService.deleteAddressById(addressId);
        return "redirect:/address";
    }
    @GetMapping("/users/addAddress")
    public String addAddress(@ModelAttribute ("address") Address address ,Model model){
        return "AddAddress";
    }

    @PostMapping("/users/addAddress")
    public String adAddress(@ModelAttribute("address") Address address, Principal principal, BindingResult result,Model model){
        if (result.hasFieldErrors()){
            model.addAttribute("address",address);
            return "AddAddress";
        }
        User user = userService.findUserByUsername(principal.getName());
        address.setUser(user);
        addressService.addAddress(address);
        return "redirect:/address";
    }
    @GetMapping("/userDetails")
    public String getUserDetails(Model model, Principal principal){
        String email = principal.getName();
        User user = userService.findUserByUsername(email);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "redirect:/userDetails";
        }
        UserDTO userDTO = userService.convertEntityToDto(user);
        userDTO.setEnabled(user.isEnabled());
        userDTO.setRole(user.getRole());
        model.addAttribute("user",userDTO);
        return "editUser";
    }
    @PostMapping("/updateUser")
    public String postUser(@ModelAttribute("user") UserDTO userDTO,Model model){
        try {
            userService.updateUser(userDTO);
            return  "redirect:/userDetails";
        } catch (DataIntegrityViolationException e){
            model.addAttribute("error","There was an error processing your request, please try again");
            return "editUser";
        }
    }
    @GetMapping("/wallet")
    public String getWallet(Model model,Principal principal){
        User user= userService.findUserByUsername(principal.getName());
        if(user.getWallet() == null ){
            Wallet wallet = new Wallet();
            user.setWallet(wallet);
        }
        if (user.getReferralCode() == null ){
            String code = generateCode();
            user.setReferralCode(code);
        }
        userRepo.save(user);
        model.addAttribute("balance",user.getWallet().getBalance());
        model.addAttribute("referral",user.getReferralCode());
        return "/User/Wallet";
    }
    private String generateCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }


}
