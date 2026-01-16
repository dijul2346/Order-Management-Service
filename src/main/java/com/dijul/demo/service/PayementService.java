package com.dijul.demo.service;
import com.dijul.demo.dto.OrderPaymentDTO;
import com.dijul.demo.exception.ResourceNotFoundException;
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
import java.util.Random;

@Slf4j
@Service
public class PayementService {
    @Autowired
    private OrderRepository repo;
    @Autowired
    private KafkaService kafkaService;

    public ResponseEntity<String> payOrder(OrderPaymentDTO orderId) {
        Order order = repo.findById(orderId.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));
        if(order.getStatus().equals(OrderStatus.PENDING_PAYMENT)) {
            if(kafkaService.addkakfaEvent(order,"payment.completed")){
                log.info("Payment Success. OrderID {} Request {}",orderId,MDC.get("correlationId"));
                return new ResponseEntity<>("Payment Completed Successfully", HttpStatus.OK);
            }
            else{
                log.info("Payment failed. OrderID {} Request {}",orderId,MDC.get("correlationId"));
                return new ResponseEntity<>("Payment Failed", HttpStatus.BAD_REQUEST);
            }
        }
        else{
            log.info("Payment request from {}, Already paid", MDC.get("correlationId"));
            return new ResponseEntity<>("Already Paid",HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
