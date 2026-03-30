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
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, CartItemRepository cartItemRepository,
                        ProductRepository productRepository, CartService cartService,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    public Long resolveUserId(String userIdentifier) {
        try {
            return Long.parseLong(userIdentifier);
        } catch (NumberFormatException e) {
            return userRepository.findByEmail(userIdentifier)
                    .map(User::getId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userIdentifier));
        }
    }

    public List<Map<String, Object>> getAllFormatted() {
        return orderRepository.findAll().stream().map(this::toAngularFormat).toList();
    }

    public List<Map<String, Object>> getByUserIdFormatted(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(this::toAngularFormat).toList();
    }

    public Optional<Map<String, Object>> getByOrderNumberFormatted(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber).map(this::toAngularFormat);
    }

    @Transactional
    public Map<String, Object> createOrder(Long userId, Map<String, Object> shippingAddress,
                                            String paymentMethod) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + product.getName());
            }
        }

        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        int total = cartItems.stream().mapToInt(i -> i.getPrice() * i.getQuantity()).sum();

        OrderEntity order = new OrderEntity();
        order.setOrderNumber(orderNumber);
        order.setUserId(userId);
        order.setTotal(total);
        order.setStatus("pending");
        order.setShippingFullName((String) shippingAddress.getOrDefault("fullName", ""));
        order.setShippingAddress((String) shippingAddress.getOrDefault("address", ""));
        order.setShippingCity((String) shippingAddress.getOrDefault("city", ""));
        order.setShippingState((String) shippingAddress.getOrDefault("state", ""));
        order.setShippingZipCode((String) shippingAddress.getOrDefault("zipCode", ""));
        order.setShippingCountry((String) shippingAddress.getOrDefault("country", ""));
        order.setShippingPhone((String) shippingAddress.getOrDefault("phone", null));
        order.setPaymentType(paymentMethod);

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

            Product product = productRepository.findById(cartItem.getProductId()).get();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }
        order.setItems(orderItems);

        OrderEntity savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);

        return toAngularFormat(savedOrder);
    }

    @Transactional
    public Map<String, Object> updateStatus(String orderNumber, String status) {
        OrderEntity order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return toAngularFormat(orderRepository.save(order));
    }

    @Transactional
    public void delete(String orderNumber) {
        orderRepository.deleteById(orderNumber);
    }

    public Optional<OrderEntity> getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    /**
     * Converts OrderEntity to Angular's Order interface format:
     * { orderNumber, items: CartItem[], total, date, status, shippingAddress, paymentMethod, createdAt, updatedAt }
     */
    private Map<String, Object> toAngularFormat(OrderEntity order) {
        // Map OrderItems to Angular CartItem format
        List<Map<String, Object>> items = order.getItems() != null
            ? order.getItems().stream().map(item -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", item.getProductId());
                map.put("name", item.getProductName());
                map.put("price", item.getPrice());
                map.put("originalPrice", item.getOriginalPrice());
                map.put("discount", item.getDiscount());
                map.put("image", item.getImage());
                map.put("quantity", item.getQuantity());
                map.put("maxStock", 999);
                return map;
            }).toList()
            : Collections.emptyList();

        // Nested shippingAddress
        Map<String, Object> shippingAddress = new LinkedHashMap<>();
        shippingAddress.put("fullName", order.getShippingFullName());
        shippingAddress.put("address", order.getShippingAddress());
        shippingAddress.put("city", order.getShippingCity());
        shippingAddress.put("state", order.getShippingState());
        shippingAddress.put("zipCode", order.getShippingZipCode());
        shippingAddress.put("country", order.getShippingCountry());
        shippingAddress.put("phone", order.getShippingPhone());

        // Nested paymentMethod
        Map<String, Object> paymentMethod = new LinkedHashMap<>();
        paymentMethod.put("type", order.getPaymentType());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderNumber", order.getOrderNumber());
        result.put("items", items);
        result.put("total", order.getTotal());
        result.put("date", order.getCreatedAt() != null ? order.getCreatedAt().toString() : null);
        result.put("status", order.getStatus());
        result.put("shippingAddress", shippingAddress);
        result.put("paymentMethod", paymentMethod);
        result.put("createdAt", order.getCreatedAt() != null ? order.getCreatedAt().toString() : null);
        result.put("updatedAt", order.getUpdatedAt() != null ? order.getUpdatedAt().toString() : null);
        return result;
    }
}
