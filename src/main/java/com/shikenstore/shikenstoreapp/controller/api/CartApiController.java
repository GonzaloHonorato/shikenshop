package com.shikenstore.shikenstoreapp.controller.api;

import com.shikenstore.shikenstoreapp.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;

    public CartApiController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getCart(@PathVariable String userId) {
        Long uid = cartService.resolveUserId(userId);
        Map<String, Object> cart = cartService.getCart(uid);
        return ResponseEntity.ok(Map.of("success", true, "data", cart));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<Map<String, Object>> addToCart(@PathVariable String userId, @RequestBody Map<String, Object> body) {
        Long uid = cartService.resolveUserId(userId);
        String productId = (String) body.get("productId");
        int quantity = body.containsKey("quantity") ? ((Number) body.get("quantity")).intValue() : 1;
        Map<String, Object> cart = cartService.addToCart(uid, productId, quantity);
        return ResponseEntity.ok(Map.of("success", true, "data", cart, "message", "Item added to cart"));
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<Map<String, Object>> updateCartItem(@PathVariable String userId, @RequestBody Map<String, Object> body) {
        Long uid = cartService.resolveUserId(userId);
        String productId = (String) body.get("productId");
        int quantity = ((Number) body.get("quantity")).intValue();
        Map<String, Object> cart = cartService.updateCartItemByProductId(uid, productId, quantity);
        return ResponseEntity.ok(Map.of("success", true, "data", cart, "message", "Cart updated"));
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<Map<String, Object>> removeFromCart(@PathVariable String userId, @PathVariable String productId) {
        Long uid = cartService.resolveUserId(userId);
        Map<String, Object> cart = cartService.removeFromCartByProductId(uid, productId);
        return ResponseEntity.ok(Map.of("success", true, "data", cart, "message", "Item removed"));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Map<String, Object>> clearCart(@PathVariable String userId) {
        Long uid = cartService.resolveUserId(userId);
        cartService.clearCart(uid);
        return ResponseEntity.ok(Map.of("success", true, "message", "Cart cleared"));
    }
}
