package com.dijul.demo.controller;

import com.dijul.demo.object.Order;
import com.dijul.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("/new")
    public Order order(@RequestBody Order order) {
        System.out.println(order);
        return orderService.createOrder(order);
    }



    @GetMapping("/view/order/{orderId}")
    public Order viewOrder(@PathVariable("orderId") UUID orderId) {
        return orderService.viewOrder(orderId);
    }

    @DeleteMapping("/{orderId}")
    public  String deleteOrder(@PathVariable("orderId") UUID orderId) {
        return orderService.DeleteOrder(orderId);
    }

    @GetMapping("/{customerId}")
    public Iterable<Order> getOrder(@PathVariable("customerId") String customerId) {
        return orderService.viewCustomerOrders(customerId);
    }

}
