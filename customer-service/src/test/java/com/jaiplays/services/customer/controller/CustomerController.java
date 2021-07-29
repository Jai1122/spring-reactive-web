package com.jaiplays.services.customer.controller;

import com.jaiplays.services.customer.dto.CustomerDTO;
import com.jaiplays.services.customer.dto.CustomerForm;
import com.jaiplays.services.customer.exception.CustomerNotFoundException;
import com.jaiplays.services.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public Mono<ResponseEntity<CustomerDTO>> getCustomer(@RequestParam String number){
        log.info("Obtained Get Customer {}",number);
        return customerService.findByNumber(number).map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<CustomerDTO>> createCustomer(@RequestBody CustomerForm customerForm){
        log.info("Obtained add customer {}",customerForm);
        return customerService.addCustomer(customerForm).map(ResponseEntity::ok);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Void> handleCustomerNotFoundException(CustomerNotFoundException ex){
        log.error("Customer not found "+ex);
        return ResponseEntity.notFound().build();
    }
}
