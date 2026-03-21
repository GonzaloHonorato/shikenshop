package com.shikenstore.shikenstoreapp.service;

import com.shikenstore.shikenstoreapp.model.Product;
import com.shikenstore.shikenstoreapp.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllActive() {
        return productRepository.findByActiveTrue();
    }

    public List<Product> getFeatured() {
        return productRepository.findByActiveTrueAndFeaturedTrue();
    }

    public List<Product> getByCategory(String category) {
        return productRepository.findByActiveTrueAndCategory(category);
    }

    public List<Product> search(String query) {
        return productRepository.searchProducts(query);
    }

    public Optional<Product> getById(String id) {
        return productRepository.findById(id);
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product update(String id, Product product) {
        product.setId(id);
        return productRepository.save(product);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }
}
