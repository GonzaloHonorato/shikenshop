package com.shikenstore.shikenstoreapp.controller.api;

import com.shikenstore.shikenstoreapp.model.OrderEntity;
import com.shikenstore.shikenstoreapp.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(@RequestParam(required = false) Long userId) {
        List<OrderEntity> orders = (userId != null) ? orderService.getByUserId(userId) : orderService.getAll();
        return ResponseEntity.ok(Map.of("success", true, "data", orders));
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<Map<String, Object>> getByOrderNumber(@PathVariable String orderNumber) {
        return orderService.getByOrderNumber(orderNumber)
                .map(o -> ResponseEntity.ok(Map.of("success", (Object) true, "data", (Object) o)))
                .orElse(ResponseEntity.status(404).body(Map.of("success", false, "error", "Order not found")));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        Long userId = ((Number) body.get("userId")).longValue();

        @SuppressWarnings("unchecked")
        Map<String, String> shipping = (Map<String, String>) body.get("shippingAddress");
        @SuppressWarnings("unchecked")
        Map<String, String> payment = (Map<String, String>) body.get("paymentMethod");

        OrderEntity order = orderService.createOrder(
                userId,
                shipping.get("fullName"),
                shipping.get("address"),
                shipping.get("city"),
                shipping.get("state"),
                shipping.get("zipCode"),
                shipping.get("country"),
                shipping.getOrDefault("phone", null),
                payment.get("type")
        );

        return ResponseEntity.ok(Map.of("success", true, "data", order, "message", "Order created"));
    }

    @PutMapping("/{orderNumber}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable String orderNumber, @RequestBody Map<String, String> body) {
        OrderEntity order = orderService.updateStatus(orderNumber, body.get("status"));
        return ResponseEntity.ok(Map.of("success", true, "data", order, "message", "Status updated"));
    }

    @DeleteMapping("/{orderNumber}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String orderNumber) {
        return orderService.getByOrderNumber(orderNumber)
                .map(o -> {
                    orderService.delete(orderNumber);
                    return ResponseEntity.ok(Map.of("success", (Object) true, "message", (Object) "Order deleted"));
                })
                .orElse(ResponseEntity.status(404).body(Map.of("success", false, "error", "Order not found")));
    }
}
