package com.dijul.demo.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderID {
    @NotNull(message = "CustomerID should not be null")
    private UUID orderId;
}
