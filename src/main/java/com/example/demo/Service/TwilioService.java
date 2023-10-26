package com.example.demo.Service;

import com.example.demo.Configuration.TwilioConfig;
import com.example.demo.dto.OtpStatus;
import com.example.demo.dto.TwilioDto;
import com.example.demo.dto.TwilioResponseDto;
import com.example.demo.dto.UserDTO;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.chat.v1.service.User;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class TwilioService {
    @Autowired
    TwilioConfig twilioConfig;
    Map<String,String> otpMap = new HashMap<>();



    public Mono<TwilioResponseDto> sendOtpReg(UserDTO userDTO){
        TwilioResponseDto twilioResponseDto =null;
        try {
            String number = "+91"+ userDTO.getPhoneNumber();
            String from = "+12518508910";
            String otp = generateOtp();
            String otpMessage = "hey, your otp is  " + otp + "please complete registration";
            Message message = Message.creator(new PhoneNumber(number), new PhoneNumber(from), otpMessage).create();
            System.out.println("Message SID: " + message.getSid()+ " " +number );
            otpMap.put(userDTO.getEmail(),otp);
            twilioResponseDto = new TwilioResponseDto(OtpStatus.DELIVERED, otpMessage);
        } catch (Exception e){
            e.printStackTrace();
            twilioResponseDto = new TwilioResponseDto(OtpStatus.FAIL,e.getMessage());
        }
        return Mono.just(twilioResponseDto);
    }
    public Mono<String> validateOtp(String userInputotp,String email){
        if (userInputotp.equals(otpMap.get(email))){
           return Mono.just("Otp is valid, continue.");
        }else {
            return Mono.error(new IllegalArgumentException( "Invalid otp"));
        }
    }
    private String generateOtp(){
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }
}
