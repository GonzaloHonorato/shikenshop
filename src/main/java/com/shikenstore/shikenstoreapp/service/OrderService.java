package com.shikenstore.shikenstoreapp.service;

import com.shikenstore.shikenstoreapp.model.*;
import com.shikenstore.shikenstoreapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, CartItemRepository cartItemRepository,
                        ProductRepository productRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    public List<OrderEntity> getAll() {
        return orderRepository.findAll();
    }

    public List<OrderEntity> getByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Optional<OrderEntity> getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Transactional
    public OrderEntity createOrder(Long userId, String shippingFullName, String shippingAddress,
                                    String shippingCity, String shippingState, String shippingZipCode,
                                    String shippingCountry, String shippingPhone, String paymentType) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Verify stock
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + product.getName());
            }
        }

        // Generate order number
        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);

        // Calculate total
        int total = cartItems.stream().mapToInt(i -> i.getPrice() * i.getQuantity()).sum();

        // Create order
        OrderEntity order = new OrderEntity();
        order.setOrderNumber(orderNumber);
        order.setUserId(userId);
        order.setTotal(total);
        order.setStatus("pending");
        order.setShippingFullName(shippingFullName);
        order.setShippingAddress(shippingAddress);
        order.setShippingCity(shippingCity);
        order.setShippingState(shippingState);
        order.setShippingZipCode(shippingZipCode);
        order.setShippingCountry(shippingCountry);
        order.setShippingPhone(shippingPhone);
        order.setPaymentType(paymentType);

        // Create order items and reduce stock
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setOriginalPrice(cartItem.getOriginalPrice());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setImage(cartItem.getImage());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);

            // Reduce stock
            Product product = productRepository.findById(cartItem.getProductId()).get();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }
        order.setItems(orderItems);

        OrderEntity savedOrder = orderRepository.save(order);

        // Clear cart
        cartService.clearCart(userId);

        return savedOrder;
    }

    @Transactional
    public OrderEntity updateStatus(String orderNumber, String status) {
        OrderEntity order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public void delete(String orderNumber) {
        orderRepository.deleteById(orderNumber);
    }
}
