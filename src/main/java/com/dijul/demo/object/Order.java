package com.dijul.demo.object;



import com.aerospike.client.query.IndexType;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.aerospike.annotation.Indexed;
import org.springframework.data.annotation.Id;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.aerospike.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private UUID orderId;
    @Indexed(type = IndexType.STRING, name = "customer_id_idx")
    @NotNull(message = "CustomerId cannot be null")
    @NotBlank(message = "CustomerId cannot be blank")
    private String customerId;
    @NotEmpty(message = "OrderList ShouldNot be empty")
    @Valid
    private List<OrderItem> items;
    private Double subtotal;
    private Double total;
    private Double taxRate;
    private Double taxAmount;
    private Double totalAmount;
    private OrderStatus status;
    @Indexed(type = IndexType.STRING, name="created_at_idx")
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}