package com.dijul.demo.controller;

import com.dijul.demo.dto.OrderItemDTO;
import com.dijul.demo.dto.OrderRequestDTO;
import com.dijul.demo.dto.OrderResponseDTO;
import com.dijul.demo.dto.PaginationDTO;
import com.dijul.demo.exception.ResourceNotFoundException;
import com.dijul.demo.model.OrderItem;
import com.dijul.demo.model.OrderStatus;
import com.dijul.demo.service.OrderService;
import com.dijul.demo.service.PayementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private PayementService payementService;

    private OrderRequestDTO orderRequestDTO;
    private OrderResponseDTO orderResponseDTO;
    private UUID orderId;
    private String customerId;


    @BeforeEach
    void setUp(){
        orderId=UUID.randomUUID();
        customerId="random-123";

        OrderItemDTO item = new OrderItemDTO();
        item.setQuantity(2);
        item.setPricePerUnit(100);
        item.setProductId("prod-X");

        List<OrderItemDTO> items= new ArrayList<>();
        items.add(item);

        orderRequestDTO= new OrderRequestDTO();
        orderRequestDTO.setItems(items);
        orderRequestDTO.setTaxRate(0.10);
        orderRequestDTO.setCustomerId(customerId);

        orderResponseDTO=new OrderResponseDTO();
        orderResponseDTO.setOrderId(orderId);
        orderResponseDTO.setStatus(OrderStatus.PENDING_PAYMENT);
        orderResponseDTO.setCustomerId(customerId);
        orderResponseDTO.setTotalAmount(220.0);

    }


    @Test
    void createOrder() throws Exception {
        when(orderService.createOrder(orderRequestDTO,Boolean.TRUE))
                .thenReturn(ResponseEntity.ok(orderResponseDTO));

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.totalAmount").value(220.0));
    }

    @Test
    void createOrder_Fail() throws Exception {
        when(orderService.createOrder(orderRequestDTO,Boolean.FALSE))
                .thenThrow(new ResourceNotFoundException("Could not create order"));

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()instanceof ResourceNotFoundException))
                .andExpect(content().string((containsString("Could not create order"))));
    }

    @Test
    void viewOrder() throws Exception{
        when(orderService.viewOrder(orderId))
                .thenReturn(ResponseEntity.ok(orderResponseDTO));

        mockMvc.perform(get("/orders/{orderId}",orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.orderId").value(orderId.toString()));

    }

    @Test
    void viewOrder_Fail() throws Exception{
        when(orderService.viewOrder(orderId))
                .thenThrow(new ResourceNotFoundException("Invalid OrderID"));

        mockMvc.perform(get("/orders/{orderId}",orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()instanceof ResourceNotFoundException))
                .andExpect(content().string(containsString("Invalid OrderID")));
    }

    @Test
    void getOrders() throws Exception{
        PaginationDTO paginationDTO= new PaginationDTO(
                Collections.singletonList(orderResponseDTO),
                1L,
                0,
                10,
                1
        );

        when(orderService.viewCustomerOrders(anyString(),anyInt(),anyInt()))
                .thenReturn(ResponseEntity.ok(paginationDTO));

        mockMvc.perform(get("/customers/{customerId}/orders",customerId)
                .param("page","0")
                .param("pageSize","10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order[0].customerId").value(customerId));
    }


    @Test
    void deleteOrder_Success() throws Exception {
        when(orderService.DeleteOrder(orderId))
                .thenReturn(ResponseEntity.ok("Success"));

        mockMvc.perform(delete("/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Success"));
    }

}