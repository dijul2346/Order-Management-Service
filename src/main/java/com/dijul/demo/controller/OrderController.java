package com.dijul.demo.controller;

import com.dijul.demo.dto.OrderPaymentDTO;
import com.dijul.demo.dto.OrderRequestDTO;
import com.dijul.demo.dto.OrderResponseDTO;
import com.dijul.demo.dto.PaginationDTO;
import com.dijul.demo.service.OrderService;
import com.dijul.demo.service.PayementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
@Tag(name="orderController", description = "Controller for all order related calls")
public class OrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    PayementService payementService;

    //Create new order
    @Operation(summary = "Create an Order",description = "Pass the orderRequestDTO to create a new order. Default taxrate 10%. Use 0.10 for 10%")
    @ApiResponse(responseCode = "201",description = "Order created successfully")
    @PostMapping("orders")
    public ResponseEntity<?> order(@Valid @RequestBody OrderRequestDTO request,
                                   @RequestParam(defaultValue ="true") boolean isSuccess) {
        return orderService.createOrder(request,isSuccess);
    }

    //Get order with orderID
    @Operation(summary = "Order Details", description = "Get order details for an orderID")
    @GetMapping("orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> viewOrder(@PathVariable("orderId") UUID orderId) {
        return orderService.viewOrder(orderId);
    }

    //Delete order- Not specified in DOC
    @DeleteMapping("/{orderId}")
    public  ResponseEntity<String> deleteOrder(@PathVariable("orderId") @Valid UUID orderId) {
        return orderService.DeleteOrder(orderId);
    }


    //Get order based on customerId
    @Operation(summary = "View a Customers Orders",description = "View all orders made by the customer.")
    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<PaginationDTO> getOrder(@Valid @PathVariable("customerId") String customerId,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "1") int size)
    {
        return orderService.viewCustomerOrders(customerId,page,size);
    }


}
