package com.example.shop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateOrderRequestDTO {

    @NotNull(message = "customerId is required")
    private Long customerId;

    @NotNull(message = "Items are required")
    @NotEmpty(message = "Items must not be empty")
    @Valid
    private List<CreateOrderItemRequestDTO> items;

    public CreateOrderRequestDTO() {
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<CreateOrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<CreateOrderItemRequestDTO> items) {
        this.items = items;
    }
}
