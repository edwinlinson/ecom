package com.example.demo.Configuration;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorPayConfig {
    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient("rzp_test_Evnp1zBkKHfvaS","rjbVJRP6mfGYCR67oxAkLsUb");
    }
}
