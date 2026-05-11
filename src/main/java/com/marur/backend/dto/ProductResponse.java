package com.marur.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean isFeatured;
    private Boolean active;
    private String categoryName;
    private Long categoryId;
}