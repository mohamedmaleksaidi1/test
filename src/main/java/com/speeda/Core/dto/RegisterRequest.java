package com.speeda.Core.dto;


import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String phoneNumber;
    private String email;

}
