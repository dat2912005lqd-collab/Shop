package com.example.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateOrderStatusRequestDTO {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(PENDING|CONFIRMED|SHIPPING|COMPLETED|CANCELED)$", message = "Status must be one of PENDING, CONFIRMED, SHIPPING, COMPLETED, CANCELED")
    private String status;

    public UpdateOrderStatusRequestDTO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
