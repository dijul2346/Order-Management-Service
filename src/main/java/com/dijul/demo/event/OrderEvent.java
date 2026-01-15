package com.dijul.demo.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String eventType;
    private UUID orderId;
    private String timestamp;
    private String correlationId;
    private String customerId;
    private Double subtotal;
    private Double taxAmount;
    private Double totalAmount;
}
