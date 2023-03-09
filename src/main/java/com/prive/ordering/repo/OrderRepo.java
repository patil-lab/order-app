package com.prive.ordering.repo;


import com.prive.ordering.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepo extends JpaRepository<OrderEntity, Long> {
	Optional<OrderEntity> findByOrderId(String orderId);
	Optional<OrderEntity> findByRequestId(String  requestid);
}
