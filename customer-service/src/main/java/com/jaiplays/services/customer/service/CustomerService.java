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
    private final CustomerRepository customerRepository;

    /**
     * Returns customer info by number
     *
     * @param number the phone number of customer
     * @return customer details
     */
    @NewSpan
    public Mono<CustomerDTO> findByNumber(String number) {
        return customerRepository.findByNumber(number)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found")))
                .map(customer -> CustomerDTO.builder()
                        .accountId(customer.getId())
                        .firstName(customer.getFirstName())
                        .lastName(customer.getLastName())
                        .email(customer.getEmail())
                        .build());

    }

    /**
     * Creates new customer in the DB
     *
     * @param customerForm the form containing customer details
     * @return Customer Entity Mono
     */
    @NewSpan
    public Mono<CustomerDTO> insertCustomer(CustomerForm customerForm) {
        return customerRepository.save(Customer.builder()
                .email(customerForm.getEmail())
                .firstName(customerForm.getFirstName())
                .lastName(customerForm.getLastName())
                .createdAt(LocalDateTime.now())
                .number(customerForm.getNumber())
                .build())
                .map(customer -> CustomerDTO.builder()
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .email(customer.getEmail())
                    .accountId(customer.getId())
                    .build());
    }
}
