package com.example.demo.Controller;

import com.example.demo.Service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;
    @GetMapping("/validate/{couponCode}")
    public ResponseEntity<Double> validateCoupon(
            @PathVariable String couponCode,
            @RequestParam("totalPrice") double totalPrice) {
        System.out.println("in validate");
        double discount = couponService.calculateDiscount(couponCode, totalPrice);
        return ResponseEntity.ok(discount);
    }
}
