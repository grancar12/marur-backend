package com.marur.backend.service;

import com.marur.backend.dto.ProductResponse;
import com.marur.backend.entity.Product;
import com.marur.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.marur.backend.entity.Category;
import com.marur.backend.dto.ProductRequest;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> getProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable).map(this::toResponse);
    }

    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable).map(this::toResponse);
    }

    public Page<ProductResponse> searchProducts(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable).map(this::toResponse);
    }

    public ProductResponse getFeatured() {
        return productRepository.findByIsFeaturedTrueAndActiveTrue()
                .map(this::toResponse)
                .orElse(null);
    }

    public ProductResponse getById(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    private ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setImageUrl(product.getImageUrl());
        response.setIsFeatured(product.getIsFeatured());
        response.setActive(product.getActive());
        if (product.getCategory() != null) {
            response.setCategoryName(product.getCategory().getName());
            response.setCategoryId(product.getCategory().getId());
        }
        return response;
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        return save(product, request);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findById(id);
        return save(product, request);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    private ProductResponse save(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setIsFeatured(request.getIsFeatured());
        product.setActive(request.getActive());
        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            product.setCategory(category);
        }
        return toResponse(productRepository.save(product));
    }
}