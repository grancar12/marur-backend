package com.marur.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private BigDecimal price;
    private String imageUrl;
    private Boolean isFeatured = false;
    private Boolean active = true;
    private Long categoryId;
}