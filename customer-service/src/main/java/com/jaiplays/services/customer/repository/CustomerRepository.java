package com.jaiplays.services.customer.repository;

import com.jaiplays.services.customer.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer,Long> {
    Mono<Customer> findByNumber(String number);
}
