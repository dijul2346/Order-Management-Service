package com.dijul.demo.service;
import com.dijul.demo.dto.OrderPaymentDTO;
import com.dijul.demo.model.Order;
import com.dijul.demo.model.OrderStatus;
import com.dijul.demo.repo.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PayementService {
    @Autowired
    private OrderRepository repo;
    @Autowired
    private KafkaService kafkaService;

    public ResponseEntity<String> payOrder(OrderPaymentDTO orderId) {
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
            log.info("Payment request from {}, Already paid", MDC.get("correlationId"));
            return new ResponseEntity<>("Already Paid",HttpStatus.NOT_ACCEPTABLE);
        }

    }
}
