package com.example.shop.service;

import com.example.shop.dto.CreateOrderItemRequestDTO;
import com.example.shop.dto.CreateOrderRequestDTO;
import com.example.shop.dto.OrderItemResponseDTO;
import com.example.shop.dto.OrderResponseDTO;
import com.example.shop.dto.UpdateOrderStatusRequestDTO;
import com.example.shop.entity.Products;
import com.example.shop.entity.order_items;
import com.example.shop.entity.orders;
import com.example.shop.exception.CustomerNotFoundException;
import com.example.shop.exception.InvalidOrderStatusException;
import com.example.shop.exception.OrderNotFoundException;
import com.example.shop.exception.OutOfStockException;
import com.example.shop.exception.ProductNotFoundException;
import com.example.shop.repository.CustomerRepository;
import com.example.shop.repository.OrderItemRepository;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            CustomerRepository customerRepository,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    private static final List<String> VALID_ORDER_STATUSES = List.of("PENDING", "CONFIRMED", "SHIPPING", "COMPLETED", "CANCELED");

    @Override
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequestDTO requestDTO) {
        if (requestDTO.getCustomerId() == null) {
            throw new IllegalArgumentException("CustomerId is required");
        }

        if (!customerRepository.existsById(requestDTO.getCustomerId())) {
            throw new CustomerNotFoundException(requestDTO.getCustomerId());
        }

        if (requestDTO.getItems() == null || requestDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be empty");
        }

        orders order = new orders();
        order.setCustomerId(requestDTO.getCustomerId());
        order.setStatus("PENDING");
        order.setTotalAmount(BigDecimal.ZERO);
        orders savedOrder = orderRepository.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<order_items> savedItems = new ArrayList<>();

        for (CreateOrderItemRequestDTO itemDTO : requestDTO.getItems()) {
            if (itemDTO.getProductId() == null) {
                throw new IllegalArgumentException("ProductId is required for each order item");
            }
            if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0 for each order item");
            }

            Products product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(itemDTO.getProductId()));

            if (!"ACTIVE".equals(product.getStatus())) {
                throw new IllegalArgumentException("Product must be ACTIVE to create order: " + product.getId());
            }

            if (itemDTO.getQuantity() > product.getStockQuantity()) {
                throw new OutOfStockException(product.getId());
            }

            product.setStockQuantity(product.getStockQuantity() - itemDTO.getQuantity());
            productRepository.save(product);

            BigDecimal unitPrice = BigDecimal.valueOf(product.getPrice());
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));

            order_items orderItem = new order_items();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setLineTotal(lineTotal);
            savedItems.add(orderItemRepository.save(orderItem));

            totalAmount = totalAmount.add(lineTotal);
        }

        savedOrder.setTotalAmount(totalAmount);
        orderRepository.save(savedOrder);

        return toResponse(savedOrder, savedItems);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        orders order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return toResponse(order);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long id, UpdateOrderStatusRequestDTO requestDTO) {
        orders order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (requestDTO.getStatus() == null || !VALID_ORDER_STATUSES.contains(requestDTO.getStatus())) {
            throw new InvalidOrderStatusException(requestDTO.getStatus());
        }

        order.setStatus(requestDTO.getStatus());
        return toResponse(orderRepository.save(order));
    }

    private OrderResponseDTO toResponse(orders order) {
        List<order_items> items = orderItemRepository.findByOrderId(order.getId());
        return toResponse(order, items);
    }

    private OrderResponseDTO toResponse(orders order, List<order_items> items) {
        List<OrderItemResponseDTO> itemResponses = items.stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()))
                .collect(Collectors.toList());

        return new OrderResponseDTO(
                order.getId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getStatus(),
                itemResponses);
    }
}
