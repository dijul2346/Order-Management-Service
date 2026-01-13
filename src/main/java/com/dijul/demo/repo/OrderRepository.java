package com.dijul.demo.repo;

import com.dijul.demo.object.Order;
import org.springframework.data.aerospike.repository.AerospikeRepository;

import java.util.UUID;

public interface OrderRepository extends AerospikeRepository<Order, UUID> {
    // Spring generates the logic for this automatically!
    Iterable<Order> findByCustomerId(String CustomerId);

}
