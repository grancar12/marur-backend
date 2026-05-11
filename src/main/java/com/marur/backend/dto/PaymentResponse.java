package com.marur.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentResponse {
    private Long id;
    private String gateway;
    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private String paymentUrl;
}