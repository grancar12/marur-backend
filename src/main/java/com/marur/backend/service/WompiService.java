package com.marur.backend.service;

import com.marur.backend.dto.PaymentResponse;
import com.marur.backend.entity.Order;
import com.marur.backend.entity.Payment;
import com.marur.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WompiService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    @Value("${wompi.public-key}")
    private String publicKey;

    @Value("${wompi.private-key}")
    private String privateKey;

    @Value("${wompi.events-key}")
    private String eventsKey;

    @Value("${wompi.redirect-url}")
    private String redirectUrl;

    public PaymentResponse createPayment(Order order, String paymentMethod) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotal());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("PENDING");
        payment.setGateway("WOMPI");
        payment.setCurrency("COP");
        paymentRepository.save(payment);

        String reference = "MARUR-" + order.getId() + "-" + UUID.randomUUID().toString().substring(0, 8);
        payment.setTransactionId(reference);
        paymentRepository.save(payment);

        String paymentUrl = buildWompiUrl(order, reference, paymentMethod);

        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setGateway("WOMPI");
        response.setTransactionId(reference);
        response.setAmount(order.getTotal());
        response.setCurrency("COP");
        response.setStatus("PENDING");
        response.setPaymentMethod(paymentMethod);
        response.setPaymentUrl(paymentUrl);
        return response;
    }

    private String buildWompiUrl(Order order, String reference, String paymentMethod) {
        long amountInCents = order.getTotal().multiply(java.math.BigDecimal.valueOf(100)).longValue();
        return "https://checkout.wompi.co/p/?public-key=" + publicKey
                + "&currency=COP"
                + "&amount-in-cents=" + amountInCents
                + "&reference=" + reference
                + "&redirect-url=" + redirectUrl
                + "&payment-method[type]=" + paymentMethod;
    }

    public void handleWebhook(Map<String, Object> payload) {
        String transactionId = (String) ((Map<?, ?>) payload.get("data")).get("transaction");
        String status = (String) ((Map<?, ?>) payload.get("data")).get("status");

        paymentRepository.findByTransactionId(transactionId).ifPresent(payment -> {
            payment.setStatus(status);
            payment.setGatewayResponse(payload.toString());
            paymentRepository.save(payment);

            if ("APPROVED".equals(status)) {
                payment.getOrder().setStatus("PAID");
            }
        });
    }
}