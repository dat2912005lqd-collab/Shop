package com.example.shop.repository;
import com.example.shop.entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CustomerRepository extends JpaRepository<Customers, Long> {
    
}
