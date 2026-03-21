package com.shikenstore.shikenstoreapp.config;

import com.shikenstore.shikenstoreapp.model.Product;
import com.shikenstore.shikenstoreapp.model.User;
import com.shikenstore.shikenstoreapp.repository.ProductRepository;
import com.shikenstore.shikenstoreapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public DataInitializer(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            initProducts();
        }
        if (userRepository.count() == 0) {
            initUsers();
        }
    }

    private void initProducts() {
        Product[] products = {
            createProduct("accion-1", "Cyberpunk Fury",
                "Explora una metropolis futurista llena de peligros y secretos oscuros en este RPG de accion cyberpunk.",
                "accion", 44990, 59990, 25, 150,
                "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=400",
                true, true, 4.8, 1250, "2024-03-15", "CyberGames Studio",
                "PC,PS5,Xbox Series X", "Cyberpunk,Open World,RPG"),

            createProduct("accion-2", "Warzone Elite",
                "Unite al campo de batalla en este shooter tactico multijugador con graficos ultra realistas.",
                "accion", 39990, 39990, 0, 200,
                "https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=400",
                true, false, 4.5, 890, "2024-05-20", "TacticalForce",
                "PC,PS5,Xbox Series X", "FPS,Multiplayer,Tactical"),

            createProduct("accion-3", "Street Fighter Revolution",
                "La proxima evolucion de la legendaria saga de lucha con nuevos personajes y mecanicas.",
                "accion", 50990, 59990, 15, 100,
                "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400",
                true, true, 4.9, 2100, "2024-02-10", "Capcom",
                "PC,PS5,Xbox Series X,Switch", "Fighting,Competitive,2D"),

            createProduct("rpg-1", "Dragon's Quest Legends",
                "Embarcate en una epica aventura medieval donde tus decisiones moldean el destino del reino.",
                "rpg", 54990, 54990, 0, 120,
                "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?w=400",
                true, true, 4.7, 1580, "2024-04-05", "FantasyWorks",
                "PC,PS5,Xbox Series X", "Medieval,Open World,Story-Rich"),

            createProduct("rpg-2", "Mystic Chronicles",
                "Un RPG de mundo abierto con un sistema de magia unico y combates por turnos estrategicos.",
                "rpg", 48990, 64990, 25, 90,
                "https://images.unsplash.com/photo-1542736667-069246bdbc6d?w=400",
                true, false, 4.6, 1120, "2024-06-12", "MysticSoft",
                "PC,PS5", "Turn-Based,Magic,Fantasy"),

            createProduct("rpg-3", "Skybound Odyssey",
                "Vuela por los cielos en dirigibles y explora islas flotantes en este RPG de aventuras aereas.",
                "rpg", 51990, 59990, 13, 75,
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400",
                true, true, 4.8, 1890, "2024-03-28", "SkyForge Studios",
                "PC,PS5,Xbox Series X,Switch", "Exploration,Flying,Adventure"),

            createProduct("estrategia-1", "Civilization Empire",
                "Construye tu imperio desde la edad de piedra hasta la era espacial en este juego de estrategia por turnos.",
                "estrategia", 47990, 47990, 0, 110,
                "https://images.unsplash.com/photo-1611996575749-79a3a250f948?w=400",
                true, true, 4.7, 2450, "2024-02-14", "Firaxis Games",
                "PC", "Turn-Based,Historical,Empire Building"),

            createProduct("estrategia-2", "StarCraft Genesis",
                "Comanda flotas espaciales en batallas epicas de estrategia en tiempo real.",
                "estrategia", 42990, 49990, 14, 95,
                "https://images.unsplash.com/photo-1446776877081-d282a0f896e2?w=400",
                true, false, 4.8, 1780, "2024-05-08", "Blizzard Entertainment",
                "PC", "RTS,Sci-Fi,Multiplayer"),

            createProduct("estrategia-3", "Total War Kingdoms",
                "Conquista territorios y libra batallas masivas en este juego de estrategia medieval.",
                "estrategia", 45990, 59990, 23, 80,
                "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400",
                true, true, 4.6, 1340, "2024-04-18", "Creative Assembly",
                "PC", "Medieval,War,Tactical"),

            createProduct("aventura-1", "The Last Explorer",
                "Explora ruinas antiguas y descubre civilizaciones perdidas en este juego de aventura narrativa.",
                "aventura", 52990, 52990, 0, 105,
                "https://images.unsplash.com/photo-1579373903781-fd5c0c30c4cd?w=400",
                true, true, 4.8, 1670, "2024-04-22", "ExploreGames",
                "PC,PS5,Xbox Series X", "Adventure,Puzzle,Story-Rich"),

            createProduct("aventura-2", "Uncharted Odyssey",
                "Accion y aventura en una busqueda del tesoro alrededor del mundo con graficos impresionantes.",
                "aventura", 45990, 64990, 29, 85,
                "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400",
                true, true, 4.9, 2890, "2024-01-30", "Naughty Dog",
                "PS5", "Action-Adventure,Cinematic,Exploration"),

            createProduct("aventura-3", "Tomb Seeker",
                "Resuelve puzzles antiguos y supera trampas mortales en templos olvidados.",
                "aventura", 38990, 49990, 22, 125,
                "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400",
                true, false, 4.5, 980, "2024-07-10", "Crystal Dynamics",
                "PC,PS5,Xbox Series X", "Puzzle,Exploration,Archaeology")
        };

        for (Product p : products) {
            productRepository.save(p);
        }
        System.out.println("Loaded " + products.length + " products");
    }

    private void initUsers() {
        userRepository.save(createUser("Administrador Principal", "admin@shikenshop.com", "Admin123", "admin"));
        userRepository.save(createUser("Juan Perez", "comprador@test.com", "Comprador123", "buyer"));
        userRepository.save(createUser("Maria Gomez", "maria.gomez@test.com", "Maria123", "buyer"));
        userRepository.save(createUser("Carlos Rodriguez", "carlos.rodriguez@test.com", "Carlos123", "buyer"));
        userRepository.save(createUser("Ana Silva", "ana.silva@test.com", "Ana123", "buyer"));
        System.out.println("Loaded 5 users");
    }

    private Product createProduct(String id, String name, String description, String category,
                                   int price, int originalPrice, int discount, int stock,
                                   String image, boolean active, boolean featured,
                                   double rating, int reviews, String releaseDate, String developer,
                                   String platform, String tags) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setDescription(description);
        p.setCategory(category);
        p.setPrice(price);
        p.setOriginalPrice(originalPrice);
        p.setDiscount(discount);
        p.setStock(stock);
        p.setImage(image);
        p.setActive(active);
        p.setFeatured(featured);
        p.setRating(rating);
        p.setReviews(reviews);
        p.setReleaseDate(releaseDate);
        p.setDeveloper(developer);
        p.setPlatform(platform);
        p.setTags(tags);
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        return p;
    }

    private User createUser(String name, String email, String password, String role) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(password);
        u.setRole(role);
        u.setActive(true);
        return u;
    }
}
