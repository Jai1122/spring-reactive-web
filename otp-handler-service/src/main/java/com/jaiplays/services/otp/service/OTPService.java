package com.jaiplays.services.otp.service;

import com.jaiplays.services.otp.dto.CustomerDTO;
import com.jaiplays.services.otp.dto.NotificationRequestForm;
import com.jaiplays.services.otp.dto.NotificationResultDTO;
import com.jaiplays.services.otp.dto.SendForm;
import com.jaiplays.services.otp.entity.Application;
import com.jaiplays.services.otp.entity.OTP;
import com.jaiplays.services.otp.enumeration.Channel;
import com.jaiplays.services.otp.enumeration.FaultReason;
import com.jaiplays.services.otp.enumeration.OTPStatus;
import com.jaiplays.services.otp.exception.OTPException;
import com.jaiplays.services.otp.repository.ApplicationRepository;
import com.jaiplays.services.otp.repository.OTPRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPService {
    private final OTPRepository otpRepository;
    private final ApplicationRepository applicationRepository;

    @Autowired
    @LoadBalanced
    private WebClient.Builder loadbalanced;

    @Autowired
    private WebClient.Builder webClient;

    @Value("${external.services.number-information}")
    private String numberInformationServiceUrl;

    @Value("${external.services.notifications}")
    private String notificationServiceUrl;

    /**
     * Generate and send OTP
     *
     * @param sendForm {@link com.jaiplays.services.otp.dto.SendForm}
     */
    @NewSpan
    public Mono<OTP> send(SendForm sendForm) {
        log.info("Entered send : {}", sendForm);

        String customerURI = UriComponentsBuilder.fromHttpUrl("http://customer-service/customers")
                .queryParam("number", sendForm.getMsisdn())
                .toUriString();
        String numberInfoURI = UriComponentsBuilder.fromHttpUrl(numberInformationServiceUrl)
                .queryParam("msisdn", sendForm.getMsisdn())
                .toUriString();
        Mono<CustomerDTO> customerInfo = loadbalanced.build()
                .get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> Mono.error(new OTPException("Error retrieving customer"
                                , FaultReason.CUSTOMER_ERROR)))
                .bodyToMono(CustomerDTO.class);

        Mono<String> msisdnStatus = webClient.build()
                .get()
                .uri(numberInfoURI)
                .retrieve()
                .onStatus(HttpStatus::isError,
                        clientResponse -> Mono.error(new OTPException("Error retrieving msisdn status"
                                , FaultReason.NUMBER_INFORMATION_ERROR)))
                .bodyToMono(String.class);

        Mono<Tuple2<CustomerDTO, String>> zippedCalls = Mono.zip(customerInfo, msisdnStatus);

        return zippedCalls.flatMap(resultTuple -> {
            int pin = 100000 + new Random().nextInt(900000);

            Mono<OTP> otpMono = otpRepository.save(OTP.builder()
                    .customerId(resultTuple.getT1().getAccountId())
                    .msisdn(sendForm.getMsisdn())
                    .pin(pin)
                    .createdOn(ZonedDateTime.now())
                    .expires(ZonedDateTime.now().plus(Duration.ofMinutes(1)))
                    .otpStatus(OTPStatus.ACTIVE)
                    .applicationId("PPR")
                    .attemptCount(0)
                    .build());

            Mono<NotificationResultDTO> notificationResultDTOMono = webClient.build()
                    .post()
                    .uri(notificationServiceUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(NotificationRequestForm.builder()
                            .channel(Channel.AUTO.name())
                            .destination(sendForm.getMsisdn())
                            .message(String.valueOf(pin))
                            .build()))
                    .retrieve()
                    .bodyToMono(NotificationResultDTO.class);

            return otpMono.zipWhen(otp -> notificationResultDTOMono)
                    .map(Tuple2::getT1);
        });
    }

    /**
     * Validates an OTP and updates its status as {@link OTPStatus#ACTIVE} on success
     *
     * @param otpId the OTP id
     * @param pin   the OTP PIN number
     */
    @NewSpan
    public Mono<OTP> validate(Long otpId, Integer pin) {
        log.info("Entered validation flow with arguments: {} and {}", otpId, pin);
        AtomicReference<FaultReason> faultReason = new AtomicReference<>();

        return otpRepository.findById(otpId)
                .switchIfEmpty(Mono.error(new OTPException("Error Validating OTP", FaultReason.NOT_FOUND)))
                .zipWhen(otp -> applicationRepository.findById(otp.getApplicationId()))
                .flatMap(Tuple2 -> {
                    OTP otp = Tuple2.getT1();
                    Application application = Tuple2.getT2();

                    if (otp.getAttemptCount() > application.getAttemptsAllowed()) {
                        otp.setOtpStatus(OTPStatus.TOO_MANY_ATTEMPTS);
                        faultReason.set(FaultReason.TOO_MANY_ATTEMPTS);
                    } else if (!otp.getPin().equals(pin)) {
                        faultReason.set(FaultReason.INVALID_PIN);
                    } else if (!otp.getOtpStatus().equals(OTPStatus.ACTIVE)) {
                        faultReason.set(FaultReason.INVALID_STATUS);
                    } else if (otp.getExpires().isBefore(ZonedDateTime.now())) {
                        otp.setOtpStatus(OTPStatus.EXPIRED);
                        faultReason.set(FaultReason.EXPIRED);
                    } else {
                        otp.setOtpStatus(OTPStatus.VERIFIED);
                    }

                    if (!otp.getOtpStatus().equals(OTPStatus.TOO_MANY_ATTEMPTS)) {
                        otp.setAttemptCount(otp.getAttemptCount() + 1);
                    }

                    if (otp.getOtpStatus().equals(OTPStatus.VERIFIED)) {
                        return otpRepository.save(otp);
                    } else {
                        return Mono.error(new OTPException("Error Validating OTP", faultReason.get(), otp));
                    }
                })
                .doOnError(throwable -> {
                    if (throwable instanceof OTPException) {
                        OTPException otpException = (OTPException) throwable;
                        if (!otpException.getFaultReason().equals(FaultReason.NOT_FOUND) && otpException.getOtp() != null) {
                            otpRepository.save(otpException.getOtp()).subscribe();
                        }
                    }
                });
    }

    /**
     * Resend OTP
     *
     * @param otpId    the OTP id
     * @param channels list of communication {@link Channel}
     * @param mail     user's email address to receieve notification
     */
    @NewSpan
    public Mono<OTP> resend(Long otpId, List<String> channels, String mail) {
        log.info("Entered Resend OTP flow : {} , {}, {}", otpId, channels, mail);

        return otpRepository.findById(otpId)
                .switchIfEmpty(Mono.error(new OTPException("Error resending otp", FaultReason.NOT_FOUND)))
                .zipWhen(otp -> {
                    if (otp.getOtpStatus() != OTPStatus.ACTIVE)
                        return Mono.error(new OTPException("Error resending otp", FaultReason.INVALID_STATUS));

                    List<Mono<NotificationResultDTO>> monoList = channels.stream()
                            .filter(Objects::nonNull)
                            .map(method -> webClient.build()
                                    .post()
                                    .uri(notificationServiceUrl)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .body(BodyInserters.fromValue(NotificationRequestForm.builder()
                                            .channel(method)
                                            .destination(Channel.EMAIL.name().equals(method) ? mail : otp.getMsisdn())
                                            .message(otp.getPin().toString())
                                            .build()))
                                    .retrieve()
                                    .bodyToMono(NotificationResultDTO.class))
                            .collect(Collectors.toList());
                    return Flux.merge(monoList).collectList();

                })
                .map(Tuple2::getT1);
    }

    /**
     * Returns all OTPs of a given number
     *
     * @param number the user's msisdn number
     */
    @NewSpan
    public Flux<OTP> getAllOTPs(String number) {
        log.info("Entered getAllOTPs : {}", number);
        return otpRepository.findByMsisdn(number)
                .switchIfEmpty(Mono.error(new OTPException("OTP not found", FaultReason.NOT_FOUND)));
    }

    /**
     * Read an already generated OTP
     *
     * @param otpId the OTP id
     */
    @NewSpan
    public Mono<OTP> getOtp(Long otpId) {
        log.info("Entered getOtp : {}", otpId);
        return otpRepository.findById(otpId)
                .switchIfEmpty(Mono.error(new OTPException("OTP not found ", FaultReason.NOT_FOUND)));
    }
}
