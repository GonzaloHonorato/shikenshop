package com.shikenstore.shikenstoreapp.controller.api;

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
    public ResponseEntity<Map<String, Object>> getAll(@RequestParam(required = false) String userId) {
        List<Map<String, Object>> orders;
        if (userId != null && !userId.isEmpty()) {
            Long uid = orderService.resolveUserId(userId);
            orders = orderService.getByUserIdFormatted(uid);
        } else {
            orders = orderService.getAllFormatted();
        }
        return ResponseEntity.ok(Map.of("success", true, "data", orders, "total", orders.size()));
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<Map<String, Object>> getByOrderNumber(@PathVariable String orderNumber) {
        return orderService.getByOrderNumberFormatted(orderNumber)
                .map(o -> ResponseEntity.ok(Map.of("success", (Object) true, "data", (Object) o)))
                .orElse(ResponseEntity.status(404).body(Map.of("success", false, "error", "Order not found")));
    }

    @SuppressWarnings("unchecked")
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        // Angular sends userId as string (email)
        String userIdStr = String.valueOf(body.get("userId"));
        Long uid = orderService.resolveUserId(userIdStr);

        // Angular sends shippingAddress as nested object
        Map<String, Object> shipping = (Map<String, Object>) body.get("shippingAddress");

        // Angular sends paymentMethod as string (not a map)
        String paymentType;
        Object pm = body.get("paymentMethod");
        if (pm instanceof Map) {
            paymentType = (String) ((Map<String, Object>) pm).get("type");
        } else {
            paymentType = String.valueOf(pm);
        }

        Map<String, Object> order = orderService.createOrder(uid, shipping, paymentType);
        return ResponseEntity.ok(Map.of("success", true, "data", order, "message", "Order created"));
    }

    @PutMapping("/{orderNumber}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable String orderNumber, @RequestBody Map<String, String> body) {
        Map<String, Object> order = orderService.updateStatus(orderNumber, body.get("status"));
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
