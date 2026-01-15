package com.dijul.demo.service;

import com.dijul.demo.event.OrderEvent;
import com.dijul.demo.model.Order;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class KafkaService {
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void addkakfaEvent(Order order, String topic) {
        Instant inst= Instant.now();
        OrderEvent newEvent = new OrderEvent(
                topic,
                order.getOrderId(),
                inst.toString(),
                order.getCustomerId(),
                order.getSubtotal(),
                order.getTaxAmount(),
                order.getTotalAmount()
        );
        kafkaTemplate.send(topic,order.getOrderId().toString(),newEvent);

    }

}
