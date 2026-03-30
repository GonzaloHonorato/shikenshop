package com.shikenstore.shikenstoreapp.controller.api;

import com.shikenstore.shikenstoreapp.model.User;
import com.shikenstore.shikenstoreapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UserService userService;

    public AuthApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        return userService.getByEmail(email)
                .filter(u -> u.getPassword().equals(password) && u.getActive())
                .map(u -> {
                    String token = Base64.getEncoder().encodeToString((email + ":" + System.currentTimeMillis()).getBytes());
                    Map<String, Object> userData = buildUserData(u);
                    return ResponseEntity.ok(Map.of(
                        "success", (Object) true,
                        "message", (Object) "Login successful",
                        "user", (Object) userData,
                        "token", (Object) token
                    ));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid credentials")));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (userService.existsByEmail(email)) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", "Email already registered"));
        }

        User user = new User();
        user.setName(body.get("name"));
        user.setEmail(email);
        user.setPassword(body.get("password"));
        user.setRole("buyer");
        user.setActive(true);

        User created = userService.create(user);
        String token = Base64.getEncoder().encodeToString((email + ":" + System.currentTimeMillis()).getBytes());

        Map<String, Object> userData = buildUserData(created);
        return ResponseEntity.ok(Map.of("success", true, "message", "Registration successful", "user", userData, "token", token));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token == null || token.isEmpty()) {
            return ResponseEntity.ok(Map.of("success", false));
        }
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String email = decoded.split(":")[0];
            boolean valid = userService.getByEmail(email).map(User::getActive).orElse(false);
            return ResponseEntity.ok(Map.of("success", valid));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false));
        }
    }

    private Map<String, Object> buildUserData(User u) {
        Map<String, Object> userData = new LinkedHashMap<>();
        userData.put("id", u.getId());
        userData.put("name", u.getName());
        userData.put("email", u.getEmail());
        userData.put("role", u.getRole());
        userData.put("active", u.getActive());
        userData.put("fullName", u.getFullName() != null ? u.getFullName() : u.getName());
        userData.put("phone", u.getPhone());
        userData.put("address", u.getAddress());
        userData.put("registeredAt", u.getRegisteredAt() != null ? u.getRegisteredAt().toString() : null);
        return userData;
    }
}
