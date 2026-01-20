package com.dijul.demo.service;

import com.dijul.demo.dto.OrderItemDTO;
import com.dijul.demo.dto.OrderRequestDTO;
import com.dijul.demo.dto.OrderResponseDTO;
import com.dijul.demo.dto.PaginationDTO;
import com.dijul.demo.exception.ResourceNotFoundException;
import com.dijul.demo.model.Order;
import com.dijul.demo.model.OrderItem;
import com.dijul.demo.model.OrderStatus;
import com.dijul.demo.repo.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServicesTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderRequestDTO orderRequestDTO;
    private OrderItem orderItem;
    private UUID orderId;
    private String customerId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        customerId = "cust-123";

        OrderItemDTO item = new OrderItemDTO();
        item.setQuantity(2);
        item.setPricePerUnit(100);
        item.setProductId("prod-X");

        List<OrderItemDTO> items= new ArrayList<>();
        items.add(item);

        List<OrderItem> orderItem = items.stream().map(
                orderItemDTO -> {
                    OrderItem item1 = new OrderItem();
                    item1.setProductId(orderItemDTO.getProductId());
                    item1.setQuantity(orderItemDTO.getQuantity());
                    item1.setPricePerUnit(orderItemDTO.getPricePerUnit());
                    return item1;
                }
        ).toList();

        orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setCustomerId(customerId);
        orderRequestDTO.setTaxRate(0.1);
        orderRequestDTO.setItems(items);

        order = new Order();
        order.setOrderId(orderId);
        order.setCustomerId(customerId);
        order.setTaxRate(0.1);
        order.setItems(orderItem);
        order.setSubtotal(200.0);
        order.setTaxAmount(20.0);
        order.setTotalAmount(220.0);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
    }

    @Test
    void createOrder_Success() {
        when(orderRepository.save(any(Order.class))).thenAnswer(i-> i.getArgument(0));
        when(kafkaService.addkakfaEvent(any(Order.class), eq("order.created"),anyBoolean())).thenReturn(true);
        ResponseEntity<?> response = orderService.createOrder(orderRequestDTO,anyBoolean());

        assertNotNull(response);
        assertTrue(response.getBody() instanceof OrderResponseDTO);

        OrderResponseDTO resultDto = (OrderResponseDTO) response.getBody();
        assertNotNull(resultDto.getOrderId());

        // 200 + 20 = 220
        assertEquals(220.0,resultDto.getTotalAmount());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(kafkaService, times(1)).addkakfaEvent(any(Order.class), eq("order.created"),anyBoolean());
    }

    @Test
    void createOrder_Fail() throws ResourceNotFoundException {
        when(orderRepository.save(any(Order.class))).thenAnswer(i-> i.getArgument(0));
        when(kafkaService.addkakfaEvent(any(Order.class), eq("order.created"),anyBoolean())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder(orderRequestDTO,Boolean.FALSE));

        assertEquals("Could not create order", exception.getMessage());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(kafkaService, times(1)).addkakfaEvent(any(Order.class), eq("order.created"),anyBoolean());

    }




    @Test
    void viewOrder(){
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        ResponseEntity<OrderResponseDTO> response = orderService.viewOrder(orderId);
        assertNotNull(response);
        assertEquals(response.getBody().getCustomerId(), customerId);
        assertEquals(response.getBody().getOrderId(),orderId);
        verify(orderRepository,times(1)).findById(orderId);
    }

    @Test
    void viewOrder_NotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.viewOrder(orderId));

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void viewCustomerOrders_Success() {
        List<Order> orders = new ArrayList<>();
        Pageable pageableRequest = PageRequest.of(0, 10);
        orders.add(order);
        Page<Order> pageOrders = new PageImpl<>(orders,pageableRequest,orders.size());


        when(orderRepository.findByCustomerId(eq(customerId), any(Pageable.class))).thenReturn(pageOrders);

        ResponseEntity<PaginationDTO> response = orderService.viewCustomerOrders(customerId, 0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());

        verify(orderRepository, times(1)).findByCustomerId(eq(customerId), any(Pageable.class));
    }

    @Test
    void deleteOrder_Success() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(order);

        ResponseEntity<String> response = orderService.DeleteOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success", response.getBody());

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void deleteOrder_NotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.DeleteOrder(orderId));

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).delete(any(Order.class));
    }
}
