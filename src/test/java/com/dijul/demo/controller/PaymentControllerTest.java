package com.dijul.demo.controller;

import com.dijul.demo.dto.OrderPaymentDTO;
import com.dijul.demo.service.OrderService;
import com.dijul.demo.service.PayementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestExecutionResult;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class PaymentControllerTest {
    @MockitoBean
    PayementService paymentService;

    @MockitoBean
    OrderService orderService;



    OrderPaymentDTO orderPaymentDTO;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        UUID orderId=UUID.randomUUID();
        OrderPaymentDTO orderPaymentDTO= new OrderPaymentDTO();
        orderPaymentDTO.setOrderId(orderId);
    }

    @Test
    void payOrder_Success() throws Exception{
        UUID orderId=UUID.randomUUID();
        OrderPaymentDTO orderPaymentDTO= new OrderPaymentDTO();
        orderPaymentDTO.setOrderId(orderId);
        when(paymentService.payOrder(any(OrderPaymentDTO.class),eq(true))).thenReturn(ResponseEntity.ok("Success"));

        mockMvc.perform(post("/payments/complete")
                .contentType(MediaType.APPLICATION_JSON)
                        .param("isSuccess", "true")
                        .content(objectMapper.writeValueAsString(orderPaymentDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));
    }

    @Test
    void payOrder_fail() throws Exception{
        UUID orderId=UUID.randomUUID();
        OrderPaymentDTO orderPaymentDTO= new OrderPaymentDTO();
        orderPaymentDTO.setOrderId(orderId);
        when(paymentService.payOrder(any(OrderPaymentDTO.class),eq(false))).thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post("/payments/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .param("isSuccess","false")
                .content(objectMapper.writeValueAsString(orderPaymentDTO)))
                .andExpect(status().isBadRequest());
    }
}
