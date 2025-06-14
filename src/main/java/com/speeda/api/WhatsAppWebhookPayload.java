package com.speeda.api;

public class WhatsAppWebhookPayload {
    private String phoneNumber;
    private String token;

    // getters & setters
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
