package com.example.shop.service;

import com.example.shop.dto.CreateOrderRequestDTO;
import com.example.shop.dto.OrderResponseDTO;
import com.example.shop.dto.UpdateOrderStatusRequestDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(CreateOrderRequestDTO requestDTO);
    List<OrderResponseDTO> getAllOrders();
    OrderResponseDTO getOrderById(Long id);
    OrderResponseDTO updateOrderStatus(Long id, UpdateOrderStatusRequestDTO requestDTO);
}
