package com.dijul.demo.service;

import com.dijul.demo.object.Order;
import com.dijul.demo.object.OrderID;
import com.dijul.demo.object.OrderItem;
import com.dijul.demo.object.OrderStatus;
import com.dijul.demo.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
public class OrderService {
    @Autowired
    OrderRepository repo;
    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    public ResponseEntity<?> createOrder(Order order) {
        order.setOrderId(UUID.randomUUID());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        Double total = 0.0;
        for(OrderItem item : order.getItems()) {
            total+=item.getPricePerUnit()*item.getQuantity();
        }
        order.setSubtotal(total);
        order.setTaxAmount((order.getTaxRate())*(order.getSubtotal()));
        order.setTotalAmount(order.getTaxAmount()+(order.getSubtotal()));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder =  repo.save(order);
        kafkaTemplate.send("order.created",savedOrder.getOrderId().toString(),savedOrder);
        return new ResponseEntity<>(savedOrder, HttpStatus.OK);
    }

    public ResponseEntity<?> viewOrder(UUID orderId) {
        Order ord= repo.findById(orderId).orElse(null);
        if(ord==null){
            return ResponseEntity.badRequest().body("Order ID is missing");
        }
        return new ResponseEntity<>(ord, HttpStatus.OK);
    }

    public String DeleteOrder(UUID orderId) {
        Order order = repo.findById(orderId).orElse(null);
        repo.delete(order);
        return "Order Deleted";
    }

    public ResponseEntity<Iterable<Order>> viewCustomerOrders(String customerId) {
        Iterable<Order> cust= repo.findByCustomerId(customerId);
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
            kafkaTemplate.send("order.payed",order.getOrderId().toString(),order);
            return new ResponseEntity<>("Success",HttpStatus.OK) ;
        }
        else{
            return new ResponseEntity<>("Already Paid",HttpStatus.NOT_ACCEPTABLE);
        }

    }

}

