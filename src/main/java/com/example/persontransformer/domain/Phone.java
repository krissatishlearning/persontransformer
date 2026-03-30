package com.example.persontransformer.domain;

public class Phone {

    private String phoneNumber;
    private String phoneType;

    public Phone() {
    }

    public Phone(String phoneNumber, String phoneType) {
        this.phoneNumber = phoneNumber;
        this.phoneType = phoneType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }
}
