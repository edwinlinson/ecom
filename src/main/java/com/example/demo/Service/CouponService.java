package com.example.demo.Service;

import com.example.demo.Model.Coupon;
import com.example.demo.Repository.CouponRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponService {
    @Autowired
    CouponRepo repo;

    public List<Coupon> getAllCoupons() {
        return repo.findAll();
    }

    public void addCoupon(Coupon coupon) {
        repo.save(coupon);
    }

    public void updateCoupon(Coupon coupon) {
        repo.save(coupon);
    }

    public Optional<Coupon> getCouponById(int id) {
        return repo.findById(id);
    }

    public double calculateDiscount(String couponCode, double totalPrice) {
        Coupon coupon = findCouponByCode(couponCode);
        System.out.println(" "+coupon);
        if (coupon != null && coupon.isActive() && totalPrice > coupon.getMinimum()) {
            return coupon.getDiscount();
        }
        return -1;
    }

    private Coupon findCouponByCode(String couponCode) {
        return repo.findCouponByCode(couponCode);
    }
}
