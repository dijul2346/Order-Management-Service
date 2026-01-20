package com.dijul.demo.service;

import com.dijul.demo.event.OrderEvent;
import com.dijul.demo.model.Order;
import com.dijul.demo.model.OrderStatus;
import com.dijul.demo.repo.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
public class KafkaService {
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;
    @Autowired
    private OrderRepository repo;
    Random random = new Random();
    Instant inst= Instant.now();

    public Boolean addkakfaEvent(Order order, String topic, Boolean isSuccess) {
        String finalTopic = topic;
        if (!isSuccess) {
            finalTopic = topic.equals("payment.completed") ? "payment.failed" : "order.failed";
        }
        OrderEvent newEvent = new OrderEvent(
                finalTopic,
                order.getOrderId(),
                inst.toString(),
                order.getCustomerId(),
                MDC.get("correlationId"),
                order.getSubtotal(),
                order.getTaxAmount(),
                order.getTotalAmount()
        );
        kafkaTemplate.send(finalTopic, order.getOrderId().toString(), newEvent);
        return (isSuccess);
    }
}
