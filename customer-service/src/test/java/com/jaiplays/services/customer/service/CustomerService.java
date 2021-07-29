package com.jaiplays.services.customer.service;

import com.jaiplays.services.customer.dto.CustomerDTO;
import com.jaiplays.services.customer.dto.CustomerForm;
import com.jaiplays.services.customer.entity.Customer;
import com.jaiplays.services.customer.exception.CustomerNotFoundException;
import com.jaiplays.services.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;

    /**
     * Returns customer info by number
     *
     * @param number the phine number of customer
     * @return customer details
     */
    @NewSpan
    public Mono<CustomerDTO> findByNumber(String number) {
        return repository.findByNumber(number)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer Not found")))
                .map(customer -> CustomerDTO.builder()
                        .accountId(customer.getId())
                        .firstName(customer.getFirstName())
                        .lastName(customer.getLastName())
                        .email(customer.getEmail())
                        .build());
    }

    /**
     * Create new customer in database
     *
     * @param customerForm the form containing the customer details
     * @return Customer entity mono
     */
    @NewSpan
    public Mono<CustomerDTO> addCustomer(CustomerForm customerForm) {
        return repository.save(Customer.builder()
                .email(customerForm.getEmail())
                .createdAt(LocalDateTime.now())
                .firstName(customerForm.getFirstName())
                .lastName(customerForm.getLastName())
                .number(customerForm.getNumber())
                .build())
                .map(customer -> CustomerDTO.builder()
                        .lastName(customer.getLastName())
                        .firstName(customer.getFirstName())
                        .email(customer.getEmail())
                        .accountId(customer.getId())
                        .build());
    }
}
