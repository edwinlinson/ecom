package com.example.demo.dto;

import lombok.Data;

@Data
public class TwilioResponseDto {
    private String message;
    private OtpStatus status;

    public TwilioResponseDto(OtpStatus otpStatus, String message) {
        this.status=otpStatus;
        this.message=message;
    }
}
