package com.dijul.demo.object;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItem {
    private String productId;
    private int quantity;
    private int pricePerUnit;
}