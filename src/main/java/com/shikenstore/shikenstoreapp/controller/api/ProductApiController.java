package com.shikenstore.shikenstoreapp.controller.api;

import com.shikenstore.shikenstoreapp.model.Product;
import com.shikenstore.shikenstoreapp.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean featured) {

        List<Product> products;
        if (search != null && !search.isEmpty()) {
            products = productService.search(search);
        } else if (category != null && !category.isEmpty()) {
            products = productService.getByCategory(category);
        } else if (Boolean.TRUE.equals(featured)) {
            products = productService.getFeatured();
        } else {
            products = productService.getAllActive();
        }

        return ResponseEntity.ok(Map.of("success", true, "data", products));
    }

    @GetMapping("/featured")
    public ResponseEntity<Map<String, Object>> getFeatured() {
        return ResponseEntity.ok(Map.of("success", true, "data", productService.getFeatured()));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(Map.of("success", true, "data", productService.getByCategory(category)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable String id) {
        return productService.getById(id)
                .map(p -> ResponseEntity.ok(Map.of("success", (Object) true, "data", (Object) p)))
                .orElse(ResponseEntity.status(404).body(Map.of("success", false, "error", "Product not found")));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Product product) {
        Product created = productService.create(product);
        return ResponseEntity.ok(Map.of("success", true, "data", created, "message", "Product created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable String id, @RequestBody Product product) {
        Product updated = productService.update(id, product);
        return ResponseEntity.ok(Map.of("success", true, "data", updated, "message", "Product updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        return productService.getById(id)
                .map(p -> {
                    productService.delete(id);
                    return ResponseEntity.ok(Map.of("success", (Object) true, "message", (Object) "Product deleted"));
                })
                .orElse(ResponseEntity.status(404).body(Map.of("success", false, "error", "Product not found")));
    }
}
