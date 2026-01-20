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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Value("${order.default-tax-rate}")
    private double defaultTaxRate;


    public ResponseEntity<OrderResponseDTO> createOrder(OrderRequestDTO dto, @RequestParam(defaultValue = "true")Boolean isSuccess) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomerId(dto.getCustomerId());
        double taxRate= (dto.getTaxRate() != null) ? dto.getTaxRate() : defaultTaxRate;
        order.setTaxRate(taxRate);
        List<OrderItem> items = dto.getItems().stream().map(
                orderItemDTO -> {
                    OrderItem item = new OrderItem();
                    item.setProductId(orderItemDTO.getProductId());
                    item.setQuantity(orderItemDTO.getQuantity());
                    item.setPricePerUnit(orderItemDTO.getPricePerUnit());
                    return item;
                }
        ).toList();
        if(dto.getItems() != null){
            order.setItems(items);
        }
        Double total = 0.0;
        for(OrderItem item : order.getItems()) {
            total+=item.getPricePerUnit()*item.getQuantity();
        }
        order.setTaxAmount(taxRate*total);
        order.setSubtotal(total);
        order.setTotalAmount(order.getTaxAmount()+(order.getSubtotal()));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        Order savedOrder =  repo.save(order);
        OrderResponseDTO ord = mapToOrderResponseDTO(savedOrder);
        if(kafkaService.addkakfaEvent(savedOrder,"order.created",isSuccess)){
            return new ResponseEntity<>(ord, HttpStatus.CREATED);
        }
        else{
            throw new ResourceNotFoundException("Could not create order");
        }
    }


    public ResponseEntity<OrderResponseDTO> viewOrder(UUID orderId) {
    Order ord= repo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Invalid orderId"));
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
//                order.getItems(),
//                order.getSubtotal(),
//                order.getTaxRate(),
//                order.getTaxAmount(),
                order.getTotalAmount()
//                order.getCreatedAt(),
//                order.getUpdatedAt()
        );
    }

    public ResponseEntity<String> DeleteOrder(UUID orderId) {
        Order order = repo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Invalid OrderID"));
        repo.delete(order);
        return new ResponseEntity<>("Success",HttpStatus.OK);
    }
}

