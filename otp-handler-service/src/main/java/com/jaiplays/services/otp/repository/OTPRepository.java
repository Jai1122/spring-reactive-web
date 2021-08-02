package com.jaiplays.services.otp.repository;

import com.jaiplays.services.otp.entity.OTP;
import com.jaiplays.services.otp.enumeration.OTPStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OTPRepository extends ReactiveCrudRepository<OTP, Long> {

    Flux<OTP> findByMsisdn(String number);

    Flux<OTP> findByCustomerId(Long customerId);

    //  Mono<OTP> findByIdAndStatus(Long otpId, OTPStatus status);

    //  Mono<OTP> findByIdAndPinAndStatus(Long otpId, Integer pin, OTPStatus status);
}
