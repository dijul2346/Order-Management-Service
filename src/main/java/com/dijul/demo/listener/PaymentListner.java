package com.dijul.demo.listener;

import com.dijul.demo.object.Order;
import com.dijul.demo.repo.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import static com.dijul.demo.object.OrderStatus.READY_FOR_SHIPPING;

@Slf4j
@Component
public class PaymentListner {
    @Autowired
    private OrderRepository orderRepository;

    @KafkaListener(topics = "payment.completed",groupId="order-management-group")
    public void handleShipment(Order order){
        log.info("Shipping Service: Processing Order ID: {}", order.getOrderId());
        orderRepository.findById(order.getOrderId()).ifPresentOrElse(order1 -> {
            order1.setStatus(READY_FOR_SHIPPING);
            order1.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order1);
            log.info("Order {} has been successfully SHIPPED", order1.getOrderId());
        },() ->log.error("No Order found with orderId {}", order.getOrderId()));
    }

}
