package com.jaiplays.services.numberinformationservice.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationRequestForm {

    @NotEmpty
    private String channel;
    private String email;
    private String phone;
    private String message;
}
