package com.shikenstore.shikenstoreapp.controller.api;

import com.shikenstore.shikenstoreapp.model.User;
import com.shikenstore.shikenstoreapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<Map<String, Object>> users = userService.getAll().stream()
                .map(this::stripPassword)
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("success", true, "data", users, "total", users.size()));
    }

    @GetMapping("/{email}")
    public ResponseEntity<Map<String, Object>> getByEmail(@PathVariable String email) {
        return userService.getByEmail(email)
                .map(u -> ResponseEntity.ok(Map.of("success", (Object) true, "data", (Object) stripPassword(u))))
                .orElse(ResponseEntity.status(404).body(Map.of("success", false, "error", "User not found")));
    }

    @PutMapping("/{email}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable String email, @RequestBody Map<String, Object> updates) {
        return userService.getByEmail(email).map(user -> {
            if (updates.containsKey("name")) user.setName((String) updates.get("name"));
            if (updates.containsKey("fullName")) user.setFullName((String) updates.get("fullName"));
            if (updates.containsKey("phone")) user.setPhone((String) updates.get("phone"));
            if (updates.containsKey("address")) user.setAddress((String) updates.get("address"));
            if (updates.containsKey("active")) user.setActive((Boolean) updates.get("active"));
            User updated = userService.update(user);
            return ResponseEntity.ok(Map.of("success", (Object) true, "data", (Object) stripPassword(updated), "message", (Object) "User updated"));
        }).orElse(ResponseEntity.status(404).body(Map.of("success", false, "error", "User not found")));
    }

    @PutMapping("/{email}/role")
    public ResponseEntity<Map<String, Object>> updateRole(@PathVariable String email, @RequestBody Map<String, String> body) {
        return userService.getByEmail(email).map(user -> {
            String newRole = body.get("role");
            user.setRole(newRole);
            User updated = userService.update(user);
            return ResponseEntity.ok(Map.of("success", (Object) true, "data", (Object) stripPassword(updated), "message", (Object) "Role updated"));
        }).orElse(ResponseEntity.status(404).body(Map.of("success", false, "error", "User not found")));
    }

    @PutMapping("/{email}/password")
    public ResponseEntity<Map<String, Object>> changePassword(@PathVariable String email, @RequestBody Map<String, String> body) {
        return userService.getByEmail(email).map(user -> {
            String currentPassword = body.get("currentPassword");
            String newPassword = body.get("newPassword");

            if (!user.getPassword().equals(currentPassword)) {
                return ResponseEntity.status(400).body(Map.of("success", (Object) false, "message", (Object) "Current password is incorrect"));
            }

            user.setPassword(newPassword);
            userService.update(user);
            return ResponseEntity.ok(Map.of("success", (Object) true, "message", (Object) "Password updated"));
        }).orElse(ResponseEntity.status(404).body(Map.of("success", false, "error", "User not found")));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String email) {
        return userService.getByEmail(email).map(user -> {
            userService.delete(user);
            return ResponseEntity.ok(Map.of("success", (Object) true, "message", (Object) "User deleted"));
        }).orElse(ResponseEntity.status(404).body(Map.of("success", false, "error", "User not found")));
    }

    private Map<String, Object> stripPassword(User user) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        map.put("role", user.getRole());
        map.put("active", user.getActive());
        map.put("fullName", user.getFullName());
        map.put("phone", user.getPhone());
        map.put("address", user.getAddress());
        map.put("registeredAt", user.getRegisteredAt() != null ? user.getRegisteredAt().toString() : null);
        map.put("updatedAt", user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null);
        return map;
    }
}
