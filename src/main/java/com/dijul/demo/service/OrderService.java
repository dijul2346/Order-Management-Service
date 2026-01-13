package com.dijul.demo.service;

import com.dijul.demo.object.Order;
import com.dijul.demo.object.OrderItem;
import com.dijul.demo.object.OrderStatus;
import com.dijul.demo.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public class OrderService {
    @Autowired
    OrderRepository repo;
//    public Order createOrder(Order order) {
//        System.out.println("Reached");
//        order.setOrderId(UUID.randomUUID());
//        order.setStatus(OrderStatus.PENDING_PAYMENT);
//
//       int total = order.getItems().stream()
//              .mapToInt(item -> (item.getPricePerUnit()) * item.getQuantity()).sum();
//        order.setTotalAmount(total);
//        System.out.println(total);
//
//        return repo.save(order);
//    }
public Order createOrder(Order order) {
    System.out.println("Reached");
    order.setOrderId(UUID.randomUUID());
    order.setStatus(OrderStatus.PENDING_PAYMENT);
    Double total = 0.0;
    for(OrderItem item : order.getItems()) {
        total+=item.getPricePerUnit()*item.getQuantity();
    }
    order.setSubtotal(total);
    order.setTaxAmount((order.getTaxRate())*(order.getSubtotal()));
    order.setTotalAmount(order.getTaxAmount()+(order.getSubtotal()));
    return repo.save(order);
}

    public Order viewOrder(UUID orderId) {
        return repo.findById(orderId).orElse(null);
    }

    public String DeleteOrder(UUID orderId) {
        Order order = repo.findById(orderId).orElse(null);
        repo.delete(order);
        return "Order Deleted";
    }

    public Iterable<Order> viewCustomerOrders(String customerId) {
        return repo.findByCustomerId(customerId);
    }


}

