package com.example.shop.exception;

public class OutOfStockException extends RuntimeException {
    public OutOfStockException(Long productId) {
        super("Product not enough stock for productId: " + productId);
    }
}
