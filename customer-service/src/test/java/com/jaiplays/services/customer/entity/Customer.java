package com.jaiplays.services.customer.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Customer {

    @Id
    private Long id;
    private String number;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdAt;
}
