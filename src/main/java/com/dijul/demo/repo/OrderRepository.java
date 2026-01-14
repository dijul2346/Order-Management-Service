package com.dijul.demo.repo;

import com.dijul.demo.model.Order;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends AerospikeRepository<Order, UUID> {
    // Spring generates the logic for this automatically!
    List<Order> findByCustomerId(String CustomerId);

}
