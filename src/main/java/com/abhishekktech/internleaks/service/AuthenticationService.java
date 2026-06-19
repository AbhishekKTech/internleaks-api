package com.abhishekktech.internleaks.service;

import com.abhishekktech.internleaks.entity.Role;
import com.abhishekktech.internleaks.entity.User;
import com.abhishekktech.internleaks.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // Return type ko String se Map mein badla taaki token aur credits dono bhej sakein
    public Map<String, Object> register(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);
        user.setCredits(200); // Default credits
        
        repository.save(user);
        
        String token = jwtService.generateToken(user);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("credits", user.getCredits());
        return response;
    }

    public Map<String, Object> authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        
        User user = repository.findByEmail(email).orElseThrow();
        String token = jwtService.generateToken(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("credits", user.getCredits());
        return response;
    }
}