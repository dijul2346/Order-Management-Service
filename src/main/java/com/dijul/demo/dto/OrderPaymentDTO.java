package com.dijul.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;
@Data

public class OrderPaymentDTO {
    @NotNull(message = "OrderID should not be null")
    @NotEmpty(message = "OrderID should not be null")
    private UUID orderId;
}
