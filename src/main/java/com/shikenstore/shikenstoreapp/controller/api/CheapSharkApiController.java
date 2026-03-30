package com.shikenstore.shikenstoreapp.controller.api;

import com.shikenstore.shikenstoreapp.model.Product;
import com.shikenstore.shikenstoreapp.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import java.util.*;

@RestController
@RequestMapping("/api/cheapshark")
public class CheapSharkApiController {

    private final RestClient restClient;
    private final ProductService productService;

    public CheapSharkApiController(ProductService productService) {
        this.restClient = RestClient.builder()
                .baseUrl("https://www.cheapshark.com/api/1.0")
                .build();
        this.productService = productService;
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(@RequestParam String q) {
        try {
            List<?> games = restClient.get()
                    .uri("/games?title={title}&limit=60&exact=0", q)
                    .retrieve()
                    .body(List.class);

            if (games == null) games = Collections.emptyList();

            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", games,
                "total", games.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "Error searching CheapShark: " + e.getMessage(),
                "data", Collections.emptyList(),
                "total", 0
            ));
        }
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importGames(@RequestBody Map<String, Object> body) {
        String searchTerm = (String) body.get("searchTerm");
        String category = (String) body.getOrDefault("category", "aventura");
        int limit = body.containsKey("limit") ? ((Number) body.get("limit")).intValue() : 20;

        try {
            List<Map<String, Object>> games = restClient.get()
                    .uri("/games?title={title}&limit={limit}&exact=0", searchTerm, limit)
                    .retrieve()
                    .body(List.class);

            if (games == null || games.isEmpty()) {
                return ResponseEntity.ok(Map.of("success", true, "data", Collections.emptyList(), "total", 0, "message", "No games found"));
            }

            List<Product> imported = new ArrayList<>();
            for (Map<String, Object> game : games) {
                String gameId = "cs-" + game.get("gameID");

                if (productService.getById(gameId).isPresent()) continue;

                String cheapest = String.valueOf(game.getOrDefault("cheapest", "0"));
                int priceInCents = (int)(Double.parseDouble(cheapest) * 100);

                Product product = new Product();
                product.setId(gameId);
                product.setName((String) game.get("external"));
                product.setDescription("Imported from CheapShark - " + game.get("external"));
                product.setCategory(category);
                product.setPrice(priceInCents);
                product.setOriginalPrice(priceInCents);
                product.setDiscount(0);
                product.setStock(100);
                product.setImage((String) game.get("thumb"));
                product.setActive(true);
                product.setFeatured(false);
                product.setRating(4.0);
                product.setReviews(0);
                product.setReleaseDate("");
                product.setDeveloper("Unknown");
                product.setPlatform("PC");
                product.setTags(category);

                imported.add(productService.create(product));
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", imported,
                "total", imported.size(),
                "message", imported.size() + " products imported"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "Error importing from CheapShark: " + e.getMessage()
            ));
        }
    }
}
