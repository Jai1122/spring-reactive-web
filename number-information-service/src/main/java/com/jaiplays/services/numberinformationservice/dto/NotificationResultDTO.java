package com.jaiplays.services.numberinformationservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResultDTO {
    private String status;
    private String message;
}
