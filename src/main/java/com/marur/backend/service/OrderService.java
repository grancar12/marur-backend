package com.marur.backend.service;

import com.marur.backend.dto.OrderRequest;
import com.marur.backend.dto.OrderResponse;
import com.marur.backend.dto.PaymentResponse;
import com.marur.backend.entity.*;
import com.marur.backend.repository.CartRepository;
import com.marur.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final WompiService wompiService;

    public OrderResponse createOrder(User user, OrderRequest request) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus("PENDING");

        List<OrderItem> items = cart.getItems().stream().map(cartItem -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(cartItem.getUnitPrice());
            return item;
        }).toList();

        order.setItems(items);
        orderRepository.save(order);

        PaymentResponse paymentResponse = wompiService.createPayment(order, request.getPaymentMethod());

        return toResponse(order, paymentResponse);
    }

    public List<OrderResponse> getUserOrders(User user) {
        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(order -> toResponse(order, null))
                .toList();
    }

    private OrderResponse toResponse(Order order, PaymentResponse paymentResponse) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus());
        response.setTotal(order.getTotal());
        response.setShippingAddress(order.getShippingAddress());
        response.setCreatedAt(order.getCreatedAt());
        response.setPayment(paymentResponse);
        return response;
    }
}