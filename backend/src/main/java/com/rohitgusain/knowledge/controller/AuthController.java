package com.rohitgusain.knowledge.controller;

import com.rohitgusain.knowledge.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest request) {
        return Map.of("accessToken", authService.login(request.email(), request.password()));
    }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
}
