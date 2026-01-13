package com.dijul.demo.controller;

import com.dijul.demo.object.Order;
import com.dijul.demo.object.OrderID;
import com.dijul.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("orders")
    public ResponseEntity<Order> order(@RequestBody Order order) {
        System.out.println(order);
        return orderService.createOrder(order);
    }



    @GetMapping("orders/{orderId}")
    public ResponseEntity<Order> viewOrder(@PathVariable("orderId") UUID orderId) {

        return orderService.viewOrder(orderId);
    }

    @DeleteMapping("/{orderId}")
    public  String deleteOrder(@PathVariable("orderId") UUID orderId) {
        return orderService.DeleteOrder(orderId);
    }

    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<Iterable<Order>> getOrder(@PathVariable("customerId") String customerId,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size)
    {
        //Pageable pageable = PageRequest.of(page,size,Sort.by("createdAt").descending());
        return orderService.viewCustomerOrders(customerId);
    }

    @PostMapping("/payment")
    private String payOrder(@RequestBody OrderID orderId) {
        System.out.println(orderId);
        return orderService.payOrder(orderId);

    }

}
