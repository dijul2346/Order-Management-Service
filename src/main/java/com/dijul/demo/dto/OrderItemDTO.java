package com.dijul.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderItemDTO {
    @NotBlank(message = "Product ID is required")
    private String productId;
    private int quantity;
    private int pricePerUnit;
}
