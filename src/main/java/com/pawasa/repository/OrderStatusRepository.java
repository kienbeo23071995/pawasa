package com.pawasa.repository;

import com.pawasa.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

    OrderStatus findById(long orderStatusId);
    List<OrderStatus> findByOrder_OrderIdOrderByIdDesc(Long orderId);

}

