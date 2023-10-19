package com.example.demo.Model;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderResponse implements Serializable {
    String secretKey;
    String razorpayOrderId;
    String applicationFee;
    String secretId;
    String pgName;
}
