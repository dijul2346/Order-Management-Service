package com.dijul.demo.service;

import com.dijul.demo.dto.OrderPaymentDTO;
import com.dijul.demo.model.Order;
import com.dijul.demo.model.OrderStatus;
import com.dijul.demo.repo.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServicesTest {

    @Mock
    OrderRepository repo;

    @Mock
    KafkaService kafkaService;

    @InjectMocks
    PayementService payementService;

    private OrderPaymentDTO orderPaymentDTO;
    private Order mockOrder;
    UUID orderId;


    @BeforeEach
    void setUp(){
        orderId = UUID.randomUUID();
        orderPaymentDTO = new OrderPaymentDTO();
        orderPaymentDTO.setOrderId(orderId);

        mockOrder=new Order();
        mockOrder.setOrderId(orderId);
        mockOrder.setStatus(OrderStatus.PENDING_PAYMENT);
    }

    @Test
    void payOrder_Success(){
        when(repo.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(kafkaService.addkakfaEvent(any(Order.class),eq("payment.completed"),eq(true))).thenReturn(true);
        ResponseEntity<String> response = payementService.payOrder(orderPaymentDTO,true);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Payment Completed Successfully",response.getBody());
        verify(kafkaService).addkakfaEvent(mockOrder,"payment.completed",true);
    }

    @Test
    void payOrder_Fail(){
        when(repo.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(kafkaService.addkakfaEvent(any(), anyString(), anyBoolean())).thenReturn(false);
        ResponseEntity<String> response = payementService.payOrder(orderPaymentDTO,false);

        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        assertEquals("Payment Failed",response.getBody());
    }

    @Test
    void payOrder_AlreadyPaid(){
        mockOrder.setStatus(OrderStatus.PAID);
        when(repo.findById(orderId)).thenReturn(Optional.of(mockOrder));

        ResponseEntity<String> response= payementService.payOrder(orderPaymentDTO,true);

        assertEquals(HttpStatus.NOT_ACCEPTABLE,response.getStatusCode());
        assertEquals("Already Paid",response.getBody());
    }
}
