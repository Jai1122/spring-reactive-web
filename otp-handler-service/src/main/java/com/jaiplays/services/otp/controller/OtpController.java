package com.jaiplays.services.otp.controller;

import com.jaiplays.services.otp.dto.SendForm;
import com.jaiplays.services.otp.entity.OTP;
import com.jaiplays.services.otp.service.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/v1/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OTPService otpService;

    @PostMapping
    public Mono<OTP> send(@RequestBody SendForm sendForm) {
        return otpService.send(sendForm);
    }

    @PostMapping("{otpId}")
    public Mono<OTP> resend(@PathVariable Long otpId, @RequestParam(required = false) List<String> channels,
                            @RequestParam(required = false) String mail) {
        return otpService.resend(otpId, channels, mail);
    }

    @PostMapping("/{otpId}/validate")
    public Mono<OTP> validate(@PathVariable Long otpId, @RequestParam Integer pin) {
        return otpService.validate(otpId, pin);
    }

    @GetMapping
    public Flux<OTP> getAllOtp(@RequestParam String number) {
        return otpService.getAllOTPs(number);
    }

    @GetMapping("/{otpId}")
    public Mono<OTP> get(@PathVariable Long otpId) {
        return otpService.getOtp(otpId);
    }
}
