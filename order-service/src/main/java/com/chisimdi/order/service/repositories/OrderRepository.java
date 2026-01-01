package com.chisimdi.order.service.repositories;

import com.chisimdi.order.service.models.OrderStatus;
import com.chisimdi.order.service.models.Orders;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders,Integer> {
   Page<Orders> findByUserId(int userId, Pageable pageable);
   List<Orders>findByCreatedAtBeforeAndOrderStatus(LocalDateTime localDateTime, OrderStatus orderStatus);
}
