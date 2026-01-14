package com.dijul.demo.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OrderItem {
    @NotBlank(message = "Product ID is required")
    private String productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @Positive(message = "Price must be greater than zero")
    private int pricePerUnit;
}