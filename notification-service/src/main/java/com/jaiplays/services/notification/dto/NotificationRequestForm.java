package com.jaiplays.services.notification.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
public class NotificationRequestForm {

    @NotEmpty
    private String channel;
    private String destination;
    private String message;

}
