package com.dijul.demo.controller;

import com.dijul.demo.dto.OrderRequestDTO;
import com.dijul.demo.dto.OrderResponseDTO;
import com.dijul.demo.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderService orderService;

    @Test
    void order() {
        OrderRequestDTO request = new OrderRequestDTO();
        request.setCustomerId("CUST-123");

        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(UUID.randomUUID());
        response.setCustomerId("CUST-123");

        ResponseEntity<OrderResponseDTO> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);

        when(orderService.createOrder(any(OrderRequestDTO.class))).thenReturn(responseEntity);

//        mockMvc.perform(post("/order")
//                        .header("X-Correlation-ID", "test-trace-id")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.customerId").value("CUST-123")
//        )






    }

    @Test
    void viewOrder() {
    }

    @Test
    void deleteOrder() {
    }
}