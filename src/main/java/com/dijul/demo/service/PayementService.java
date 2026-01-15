package com.dijul.demo.service;

import com.dijul.demo.dto.OrderPaymentDTO;
import com.dijul.demo.model.Order;
import com.dijul.demo.model.OrderStatus;
import com.dijul.demo.repo.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PayementService {

    @Autowired
    private OrderRepository repo;

    @Autowired
    KafkaService kafkaService;

    public ResponseEntity<String> payOrder(@Valid OrderPaymentDTO orderId) {
        Order order = repo.findById(orderId.getOrderId()).orElse(null);
        if(order==null){
            return new ResponseEntity<>("Invalid OrderId", HttpStatus.NOT_FOUND);
        }
        else if(order.getStatus().equals(OrderStatus.PENDING_PAYMENT)) {
            order.setStatus(OrderStatus.PAID);
            order.setUpdatedAt(LocalDateTime.now());
            repo.save(order);
            kafkaService.addkakfaEvent(order,"payment.completed");

            return new ResponseEntity<>("Success",HttpStatus.OK) ;
        }
        else{
            return new ResponseEntity<>("Already Paid",HttpStatus.NOT_ACCEPTABLE);
        }

    }
}
