package com.dijul.demo.service;

import com.dijul.demo.event.OrderEvent;
import com.dijul.demo.dto.OrderRequestDTO;
import com.dijul.demo.dto.OrderResponseDTO;
import com.dijul.demo.model.Order;
import com.dijul.demo.model.OrderID;
import com.dijul.demo.model.OrderItem;
import com.dijul.demo.model.OrderStatus;
import com.dijul.demo.repo.OrderRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
public class OrderService {
    @Autowired
    OrderRepository repo;
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public ResponseEntity<OrderResponseDTO> createOrder(OrderRequestDTO dto) {
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
        OrderResponseDTO ord = mapToOrderResponseDTO(savedOrder);
        addkakfaEvent(savedOrder,"order.created");
        //kafkaTemplate.send("order.created",order.getOrderId().toString(),kfk);
        return new ResponseEntity<>(ord, HttpStatus.OK);
    }

    private OrderResponseDTO mapToOrderResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getOrderId(),
                order.getStatus(),
                order.getCustomerId(),
                order.getItems(),
                order.getSubtotal(),
                order.getTotal(),
                order.getTaxRate(),
                order.getTaxAmount(),
                order.getTotalAmount(),
                order.getCreatedAt()
        );
    }

    private void addkakfaEvent(Order order, String topic) {
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
        //return "Success";
    }


    public ResponseEntity<OrderResponseDTO> viewOrder(UUID orderId) {
        Order ord= repo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Order Not Found"));
        OrderResponseDTO dto = mapToOrderResponseDTO(ord);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    public String DeleteOrder(UUID orderId) {
        Order order = repo.findById(orderId).orElse(null);
        repo.delete(order);
        return "Order Deleted";
    }

    public ResponseEntity<Page<Order>> viewCustomerOrders(String customerId,int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<Order> cust= repo.findByCustomerId(customerId,pageable);
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
            addkakfaEvent(order,"payment.completed");
            //kafkaTemplate.send("payment.completed",order.getOrderId().toString(),newEven);
            return new ResponseEntity<>("Success",HttpStatus.OK) ;
        }
        else{
            return new ResponseEntity<>("Already Paid",HttpStatus.NOT_ACCEPTABLE);
        }

    }

}

