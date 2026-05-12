package com.example.shop.repository;

import com.example.shop.entity.order_items;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<order_items, Long> {
    List<order_items> findByOrderId(Long orderId);
}
