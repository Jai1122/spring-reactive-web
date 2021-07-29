package com.jaiplays.services.numberinformationservice.controller;

import com.jaiplays.services.numberinformationservice.enums.MsisdnStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/number-information")
@RequiredArgsConstructor
public class NumberInformationController {

    @GetMapping
    public ResponseEntity<String> verifyMsisdn(@RequestParam String msisdn){
        return ResponseEntity.ok(MsisdnStatus.OK.name());
    }
}
