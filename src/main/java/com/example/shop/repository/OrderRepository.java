package com.example.shop.repository;

import com.example.shop.entity.orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<orders, Long> {
}
