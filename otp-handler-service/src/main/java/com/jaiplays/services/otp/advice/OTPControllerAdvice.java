package com.jaiplays.services.otp.advice;

import com.jaiplays.services.otp.enumeration.FaultReason;
import com.jaiplays.services.otp.exception.OTPException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class OTPControllerAdvice {

    @ExceptionHandler(OTPException.class)
    public ResponseEntity<String> handleOTPException(OTPException ex){
        final FaultReason faultReason = ex.getFaultReason()!=null? ex.getFaultReason():FaultReason.GENERIC_ERROR;
        log.error("otp-service error",ex);
        HttpStatus httpStatus;
        switch (faultReason){
            case NOT_FOUND:
                httpStatus = HttpStatus.NOT_FOUND;
                break;
            case TOO_MANY_ATTEMPTS:
                httpStatus  =   HttpStatus.BAD_REQUEST;
                break;
            default:
                httpStatus  =   HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(httpStatus).body(faultReason.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e){
        log.error("otp-service exception {}",e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server was unable to fulfil the request");
    }
}
