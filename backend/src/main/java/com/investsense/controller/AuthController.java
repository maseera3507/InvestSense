package com.investsense.controller;

import com.investsense.dto.AuthDtos.*;
import com.investsense.model.User;
import com.investsense.repository.UserRepository;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository repo, PasswordEncoder encoder) {
        this.repo=repo; this.encoder=encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if(req.name()==null || req.name().isBlank() || req.email()==null || req.password()==null || req.password().length()<6)
            return ResponseEntity.badRequest().body(new MessageResponse("Please provide a name, valid email and password of at least 6 characters."));
        if(repo.existsByEmail(req.email().toLowerCase()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("An account with this email already exists."));
        User u = repo.save(new User(req.name().trim(), req.email().toLowerCase().trim(), encoder.encode(req.password())));
        return ResponseEntity.ok(new UserResponse(u.getId(),u.getName(),u.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return repo.findByEmail(req.email().toLowerCase().trim())
            .filter(u -> encoder.matches(req.password(),u.getPassword()))
            .<ResponseEntity<?>>map(u -> ResponseEntity.ok(new UserResponse(u.getId(),u.getName(),u.getEmail())))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid email or password.")));
    }
}
