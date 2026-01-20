package com.dijul.demo.listener;

import com.dijul.demo.event.OrderEvent;
import com.dijul.demo.repo.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import static com.dijul.demo.model.OrderStatus.READY_FOR_SHIPPING;

@Slf4j
@Component
public class PaymentListner {
    @Autowired
    private OrderRepository orderRepository;

    @KafkaListener(topics = "payment.completed",groupId="order-management-group-v2")
    public void handleShipment(OrderEvent order){
        log.info("Listner started for request {}",order.getCorrelationId());
        log.info("Shipping Service: Processing Order ID: {}", order.getOrderId());
        orderRepository.findById(order.getOrderId()).ifPresentOrElse(order1 -> {
            order1.setStatus(READY_FOR_SHIPPING);
            order1.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order1);
            log.info("Order {} has been successfully SHIPPED", order1.getOrderId());
        },() ->log.error("No Order found with orderId {} request : {}", order.getOrderId(),order.getCorrelationId()));
    }

    @KafkaListener(topics = {"payment.failed","order.failed"}, groupId = "order-group")
    public void handleFailure(OrderEvent event) {
            log.error("{} for order: {} request: {}", event.getEventType(),event.getOrderId(),event.getCorrelationId());
    }

}
