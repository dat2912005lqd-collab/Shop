package com.example.shop.service;

import com.example.shop.dto.CreateProductRequestDTO;
import com.example.shop.dto.ProductResponse;
import com.example.shop.dto.UpdateProductRequestDTO;
import com.example.shop.dto.UpdateProductStatusRequestDTO;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequestDTO requestDTO);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductResponse updateProduct(Long id, UpdateProductRequestDTO requestDTO);
    ProductResponse updateProductStatus(Long id, UpdateProductStatusRequestDTO requestDTO);
}
