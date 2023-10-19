package com.example.demo.dto;


import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private int id;

    @NotBlank
    @NoWhitespace
    private String firstname;

    @NotEmpty
    @NoWhitespace
    @NotBlank
    private String lastname;


    @NotEmpty(message = "Email Should Not be Empty")
    @Email
    @NoWhitespace
    private String email;

    @NotEmpty
    @NoWhitespace
    private String password;
    private String role;

    @Size(min = 10,max = 10,message = "Phone number should be atleast 10 digit number")
    @NoWhitespace
    private String phoneNumber;

    private boolean enabled;

    private String code;
}
