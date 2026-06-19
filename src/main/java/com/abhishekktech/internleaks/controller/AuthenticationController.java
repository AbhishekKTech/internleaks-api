package com.abhishekktech.internleaks.controller;

import com.abhishekktech.internleaks.service.AuthenticationService;
import com.abhishekktech.internleaks.repository.UserRepository;
import com.abhishekktech.internleaks.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; // 👉 NAYA IMPORT
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService service;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 👉 NAYA FIELD

    // Constructor mein PasswordEncoder add kiya
    public AuthenticationController(AuthenticationService service, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = service.register(
                request.get("name"),
                request.get("email"),
                request.get("password")
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody Map<String, String> request) {
        Map<String, Object> response = service.authenticate(
                request.get("email"),
                request.get("password")
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-credits")
    public ResponseEntity<String> updateCredits(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        Integer newCredits = (Integer) request.get("credits");
        
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setCredits(newCredits);
            userRepository.save(user);
            return ResponseEntity.ok("Credits synced in DB");
        }
        return ResponseEntity.badRequest().body("User not found");
    }

    // 👉 NAYA ENDPOINT (PUT ki jagah POST aur Password Encode hoga)
    @PostMapping("/update-profile")
    public ResponseEntity<String> updateProfile(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newName = request.get("name");
        String newPassword = request.get("password");

        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            if (newName != null && !newName.isEmpty()) {
                user.setName(newName);
            }
            if (newPassword != null && !newPassword.isEmpty()) {
                // Password encode karke save karna bohot zaroori hai!
                user.setPassword(passwordEncoder.encode(newPassword));
            }
            userRepository.save(user);
            return ResponseEntity.ok("Profile updated successfully");
        }
        return ResponseEntity.badRequest().body("User not found");
    }
}