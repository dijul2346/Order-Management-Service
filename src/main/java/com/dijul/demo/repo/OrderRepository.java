package com.dijul.demo.repo;

import com.dijul.demo.model.Order;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends AerospikeRepository<Order, UUID> {
    Page<Order> findByCustomerId(String CustomerId, Pageable pageable);
}
