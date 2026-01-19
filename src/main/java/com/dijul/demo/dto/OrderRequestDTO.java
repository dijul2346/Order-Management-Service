package com.dijul.demo.dto;

import com.dijul.demo.model.OrderItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    @NotBlank(message = "CustomerId should not be empty")
    private String customerId;

    @NotEmpty(message = "Order List Should not be empty")
    @Valid
    private List<OrderItemDTO> Items;
    private Double taxRate;
}
