package com.jaiplays.services.otp.exception;

import com.jaiplays.services.otp.entity.OTP;
import com.jaiplays.services.otp.enumeration.FaultReason;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter @Setter
public class OTPException extends RuntimeException{
    public HttpStatus httpStatus;
    public FaultReason faultReason;
    public OTP otp;

    public OTPException(String message, FaultReason faultReason){
        super(message);
        this.faultReason    =   faultReason;
    }

    public OTPException(String message, FaultReason faultReason, OTP otp){
        super(message);
        this.faultReason    =   faultReason;
        this.otp = otp;
    }
}
