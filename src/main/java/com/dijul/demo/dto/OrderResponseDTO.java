package com.dijul.demo.dto;


import com.dijul.demo.model.OrderItem;
import com.dijul.demo.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {

    private UUID orderId;
    private OrderStatus status;
    private String customerId;
    //private List<OrderItem> items;
//    private Double subtotal;
//    private Double taxRate;
//    private Double taxAmount;
    private Double totalAmount;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;

}
