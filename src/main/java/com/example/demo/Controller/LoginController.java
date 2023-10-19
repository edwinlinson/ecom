package com.example.demo.Controller;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepo;
import com.example.demo.Service.TwilioService;
import com.example.demo.Service.UserServiceImpl;
import com.example.demo.dto.OtpStatus;
import com.example.demo.dto.TwilioResponseDto;
import com.example.demo.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

@Controller
@Slf4j
public class LoginController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    UserRepo repo;
    @Autowired
    TwilioService twilioService;

//    @GetMapping("/login")
//    public String loginForm(Model model) {
//        model.addAttribute("title", "Login Page");
//        return "login";
//    }
@GetMapping("/login")
public String loginForm(@RequestParam(value = "error", required = false) String error, Model model) {
    model.addAttribute("title", "Login Page");
    if (error != null) {
        System.out.println(" " +error);
        if (error.contains("exist")) {
            model.addAttribute("errorMessage", "Invalid Username or Password");
        } else if (error.contains("disabled")) {
            model.addAttribute("errorMessage", "Account is disabled");
        } else {
            model.addAttribute("errorMessage", "Unknown error");
        }
    }

    return "login";
}


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserDTO());
        return "registere";
    }

    @PostMapping("/register")
    public String registration(@Valid @ModelAttribute("userDto") UserDTO user,
                               BindingResult result,
                               Model model, RedirectAttributes redirectAttributes) {
        User existing = userService.findByEmail(user.getEmail());

        if (existing != null) {
            result.rejectValue("email", "", "There is already an account registered with that email");
        }

        if (result.hasErrors()) {
            model.addAttribute("userDto", user);
            return "registere";
        }
        TwilioResponseDto responseDto = twilioService.sendOtpReg(user).block();
        if (responseDto !=null && responseDto.getStatus() == OtpStatus.DELIVERED){
            userService.saveUser(user);
            System.out.println("otp status delivered and user registered.");
            model.addAttribute("userDto",user);
            return "OTPverification";
        }else {
            // Handle the case where OTP sending failed or was not delivered
            model.addAttribute("errorMessage", "Failed to send OTP.");
            log.info("full error" +user.getPhoneNumber() + " email" +user.getEmail());
            System.out.println("error " +user.getPhoneNumber());
            return "registere";
        }

    }

    private String generateOtp() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }
    @PostMapping("/verifyotp")
    public String verifyOTP(@RequestParam("otp")String otp, Model model, @RequestParam("email") String email){
        System.out.println("hi da " + email);
        try {
            String validation = twilioService.validateOtp(otp,email).block();
            return "registerSuccess";
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            model.addAttribute("error","inavlid otp, try again");
            return "redirect:verifyotp";
        }
    }

}
