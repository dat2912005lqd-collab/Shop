package com.example.shop.service.impl;

import com.example.shop.dto.CreateProductRequestDTO;
import com.example.shop.dto.ProductResponse;
import com.example.shop.dto.UpdateProductRequestDTO;
import com.example.shop.dto.UpdateProductStatusRequestDTO;
import com.example.shop.entity.Products;
import com.example.shop.exception.ProductNotFoundException;
import com.example.shop.repository.ProductRepository;
import com.example.shop.service.ProductService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(CreateProductRequestDTO requestDTO) {
        validateStatus(requestDTO.getStatus());

        Products product = new Products();
        product.setName(requestDTO.getName());
        product.setPrice(requestDTO.getPrice());
        product.setStockQuantity(requestDTO.getStockQuantity());
        product.setStatus(requestDTO.getStatus());
        Products saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id).map(this::toResponse).orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public ProductResponse updateProduct(Long id, UpdateProductRequestDTO requestDTO) {
        validateStatus(requestDTO.getStatus());

        Products product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.setName(requestDTO.getName());
        product.setPrice(requestDTO.getPrice());
        product.setStockQuantity(requestDTO.getStockQuantity());
        product.setStatus(requestDTO.getStatus());
        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProductStatus(Long id, UpdateProductStatusRequestDTO requestDTO) {
        validateStatus(requestDTO.getStatus());

        Products product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.setStatus(requestDTO.getStatus());
        return toResponse(productRepository.save(product));
    }

    private void validateStatus(String status) {
        if (status == null || !("ACTIVE".equals(status) || "INACTIVE".equals(status))) {
            throw new IllegalArgumentException("Status must be ACTIVE or INACTIVE");
        }
    }

    private ProductResponse toResponse(Products product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStatus()
        );
    }
}
