package com.dijul.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
@Data
@AllArgsConstructor
public class PaginationDTO {
    private List<OrderResponseDTO> order;
    private Long totalOrders;
    private int currentPage;
    private int pageSize;
    private int totalPages;
}
