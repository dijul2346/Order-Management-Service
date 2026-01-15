package com.dijul.demo.controller;

import com.dijul.demo.dto.OrderRequestDTO;
import com.dijul.demo.dto.OrderResponseDTO;
import com.dijul.demo.model.Order;
import com.dijul.demo.model.OrderID;
import com.dijul.demo.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class OrderController {
    @Autowired
    OrderService orderService;

    //Create new order
    @PostMapping("orders")
    public ResponseEntity<OrderResponseDTO> order(@Valid @RequestBody OrderRequestDTO request) {

        return orderService.createOrder(request);
    }

    //Get order with orderID
    @GetMapping("orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> viewOrder(@PathVariable("orderId") UUID orderId) {
        return orderService.viewOrder(orderId);
    }

    //Delete order- Not specified in DOC
    @DeleteMapping("/{orderId}")
    public  String deleteOrder(@PathVariable("orderId") UUID orderId) {
        return orderService.DeleteOrder(orderId);
    }


    //Get order based on customerId
    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<Page<Order>> getOrder(@Valid @PathVariable("customerId") String customerId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "1") int size)
    {
        //Pageable pageable = PageRequest.of(page,size,Sort.by("createdAt").descending());
        return orderService.viewCustomerOrders(customerId,page,size);
    }

    //Simulating payment
    @PostMapping("/payment")
    private ResponseEntity<String> payOrder(@RequestBody OrderID orderId) {
        System.out.println(orderId);
        return orderService.payOrder(orderId);

    }

}
