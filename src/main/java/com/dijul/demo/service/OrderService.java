package com.dijul.demo.service;

import com.dijul.demo.dto.OrderRequestDTO;
import com.dijul.demo.dto.OrderResponseDTO;
import com.dijul.demo.model.Order;
import com.dijul.demo.model.OrderID;
import com.dijul.demo.model.OrderItem;
import com.dijul.demo.model.OrderStatus;
import com.dijul.demo.repo.OrderRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
public class OrderService {
    @Autowired
    OrderRepository repo;
    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    public ResponseEntity<?> createOrder(OrderRequestDTO dto) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomerId(dto.getCustomerId());
        order.setTaxRate(dto.getTaxRate());
        if(dto.getItems() != null){
            order.setItems(dto.getItems());
        }
        Double total = 0.0;
        for(OrderItem item : order.getItems()) {
            total+=item.getPricePerUnit()*item.getQuantity();
        }
        order.setSubtotal(total);
        order.setTaxAmount((order.getTaxRate())*(order.getSubtotal()));
        order.setTotalAmount(order.getTaxAmount()+(order.getSubtotal()));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        Order savedOrder =  repo.save(order);
        kafkaTemplate.send("order.created",savedOrder.getOrderId().toString(),savedOrder);

        return new ResponseEntity<>(savedOrder, HttpStatus.OK);
    }

    public ResponseEntity<OrderResponseDTO> viewOrder(UUID orderId) {
        Order ord= repo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Order Not Found"));
        OrderResponseDTO dto = new OrderResponseDTO();
        if(ord != null){
            dto.setStatus(ord.getStatus());
            dto.setCustomerId(ord.getCustomerId());
            dto.setOrderId(ord.getOrderId());
            dto.setItems(ord.getItems());
            dto.setTaxRate(ord.getTaxRate());
            dto.setSubtotal(ord.getSubtotal());
            dto.setTaxAmount(ord.getTaxAmount());
            dto.setTotalAmount(ord.getTotalAmount());
            dto.setCreatedAt(ord.getCreatedAt());
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    public String DeleteOrder(UUID orderId) {
        Order order = repo.findById(orderId).orElse(null);
        repo.delete(order);
        return "Order Deleted";
    }

    public ResponseEntity<List<Order>> viewCustomerOrders(String customerId) {
        List<Order> cust= repo.findByCustomerId(customerId);
        return new ResponseEntity<>(cust, HttpStatus.OK);
    }

    public ResponseEntity<String> payOrder(OrderID orderId) {
        Order order = repo.findById(orderId.getOrderId()).orElse(null);
        if(order==null){
            return new ResponseEntity<>("Invalid OrderId",HttpStatus.NOT_FOUND);
        }
        else if(order.getStatus().equals(OrderStatus.PENDING_PAYMENT)) {
            order.setStatus(OrderStatus.PAID);
            order.setUpdatedAt(LocalDateTime.now());
            repo.save(order);
            kafkaTemplate.send("payment.completed",order.getOrderId().toString(),order);
            return new ResponseEntity<>("Success",HttpStatus.OK) ;
        }
        else{
            return new ResponseEntity<>("Already Paid",HttpStatus.NOT_ACCEPTABLE);
        }

    }

}

