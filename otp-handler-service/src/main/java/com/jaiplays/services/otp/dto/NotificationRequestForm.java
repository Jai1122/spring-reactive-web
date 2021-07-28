package com.jaiplays.services.otp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestForm {

    @NotEmpty
    private String channel;

    private String destination;

    private String message;
}
