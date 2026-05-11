package com.marur.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String status;
    private BigDecimal total;
    private String shippingAddress;
    private List<CartItemResponse> items;
    private PaymentResponse payment;
    private LocalDateTime createdAt;
}
