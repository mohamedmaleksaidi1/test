package com.speeda.Core.dto;


import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
