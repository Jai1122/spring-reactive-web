package com.jaiplays.services.otp.enumeration;

public enum FaultReason {
    EXPIRED("OTP has expired"),
    TOO_MANY_ATTEMPTS("Too many validation attempts"),
    INVALID_PIN("Wrong Pin"),
    INVALID_STATUS("Invalid Status"),
    NOT_FOUND("Resource Not Found"),
    CUSTOMER_ERROR("Customer Retrieval Failed"),
    NUMBER_INFORMATION_ERROR("MSISDM Status Check Failed"),
    GENERIC_ERROR("Server unable to fulfil the request");

    private String message;

    FaultReason(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
