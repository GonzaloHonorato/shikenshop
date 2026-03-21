package com.shikenstore.shikenstoreapp.service;

import com.shikenstore.shikenstoreapp.model.CartItem;
import com.shikenstore.shikenstoreapp.model.Product;
import com.shikenstore.shikenstoreapp.repository.CartItemRepository;
import com.shikenstore.shikenstoreapp.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Map<String, Object> getCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        return buildCartResponse(items);
    }

    @Transactional
    public Map<String, Object> addToCart(Long userId, String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(Math.min(item.getQuantity() + quantity, product.getStock()));
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setOriginalPrice(product.getOriginalPrice());
            item.setDiscount(product.getDiscount());
            item.setImage(product.getImage());
            item.setQuantity(Math.min(quantity, product.getStock()));
            item.setMaxStock(product.getStock());
            cartItemRepository.save(item);
        }

        return getCart(userId);
    }

    @Transactional
    public Map<String, Object> updateCartItem(Long userId, Long itemId, int quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(Math.min(quantity, item.getMaxStock()));
            cartItemRepository.save(item);
        }
        return getCart(userId);
    }

    @Transactional
    public Map<String, Object> removeFromCart(Long userId, Long itemId) {
        cartItemRepository.deleteById(itemId);
        return getCart(userId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    private Map<String, Object> buildCartResponse(List<CartItem> items) {
        int totalItems = items.stream().mapToInt(CartItem::getQuantity).sum();
        int subtotal = items.stream().mapToInt(i -> i.getOriginalPrice() * i.getQuantity()).sum();
        int total = items.stream().mapToInt(i -> i.getPrice() * i.getQuantity()).sum();
        int totalDiscount = subtotal - total;

        Map<String, Object> cart = new LinkedHashMap<>();
        cart.put("items", items);
        cart.put("totalItems", totalItems);
        cart.put("subtotal", subtotal);
        cart.put("totalDiscount", totalDiscount);
        cart.put("total", total);
        return cart;
    }
}
