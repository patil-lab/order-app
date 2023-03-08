package com.prive.ordering.repo;


import com.prive.ordering.entity.OrderEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepo extends CrudRepository<OrderEntity,Long> {
	Optional<OrderEntity> findByOrderId(String orderId);
	Optional<OrderEntity> findByRequestId(String  requestid);
}
