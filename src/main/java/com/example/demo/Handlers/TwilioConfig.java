//package com.example.demo.Handlers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.function.RouterFunction;
//import org.springframework.web.servlet.function.RouterFunctions;
//import org.springframework.web.servlet.function.ServerResponse;
//
//@Configuration
//public class TwilioConfig {
//    @Autowired
//    TwilioOtpHAndler otpHAndler;
//    @Bean
//    public RouterFunction<ServerResponse> handleSMS(){
//        return RouterFunctions.route()
//                .POST("/router/sendOtpReg", otpHAndler::sendOTP)
//                .POST("/router/validateOtp", otpHAndler::validateOTP);
//    }
//
//
//}
