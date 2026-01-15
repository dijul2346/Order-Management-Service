package com.dijul.demo.service;

import com.dijul.demo.dto.OrderRequestDTO;
import com.dijul.demo.dto.OrderResponseDTO;
import com.dijul.demo.dto.PaginationDTO;
import com.dijul.demo.exception.ResourceNotFoundException;
import com.dijul.demo.model.Order;
import com.dijul.demo.model.OrderItem;
import com.dijul.demo.model.OrderStatus;
import com.dijul.demo.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    OrderRepository repo;
    @Autowired
    KafkaService kafkaService;


    public ResponseEntity<OrderResponseDTO> createOrder(OrderRequestDTO dto) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomerId(dto.getCustomerId());
        order.setTaxRate(dto.getTaxRate());
        if(dto.getItems() != null){
            order.setItems(dto.getItems());
        }
        Double total = 0.0;
        for(OrderItem item : order.getItems()) {
            total+=item.getPricePerUnit()*item.getQuantity();
        }
        order.setSubtotal(total);
        order.setTaxAmount((order.getTaxRate())*(order.getSubtotal()));
        order.setTotalAmount(order.getTaxAmount()+(order.getSubtotal()));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        Order savedOrder =  repo.save(order);
        OrderResponseDTO ord = mapToOrderResponseDTO(savedOrder);
        kafkaService.addkakfaEvent(savedOrder,"order.created");
        return new ResponseEntity<>(ord, HttpStatus.OK);
    }


    public ResponseEntity<OrderResponseDTO> viewOrder(UUID orderId) {
    Order ord= repo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Inavalid orderId"));
        OrderResponseDTO dto = mapToOrderResponseDTO(ord);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    public ResponseEntity<PaginationDTO> viewCustomerOrders(String customerId,int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<Order> cust= repo.findByCustomerId(customerId,pageable);
        List<Order> orders=cust.getContent();
        List<OrderResponseDTO> orderList= new ArrayList<OrderResponseDTO>();
        for(Order order : orders){
            orderList.add(mapToOrderResponseDTO(order));
        }
        PaginationDTO paginationOP= new PaginationDTO(
                orderList,
                cust.getTotalElements(),
                cust.getPageable().getPageNumber(),
                cust.getPageable().getPageSize(),
                cust.getTotalPages()
        );
        return new ResponseEntity<>(paginationOP, HttpStatus.OK);
    }

    private OrderResponseDTO mapToOrderResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getOrderId(),
                order.getStatus(),
                order.getCustomerId(),
                order.getItems(),
                order.getSubtotal(),
                order.getTaxRate(),
                order.getTaxAmount(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public ResponseEntity<String> DeleteOrder(UUID orderId) {
        Order order = repo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Invalid OrderID"));

        repo.delete(order);
        return new ResponseEntity<>("Success",HttpStatus.OK);
    }



}

