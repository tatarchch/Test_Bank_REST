package com.example.bankcards.controller;

import com.example.bankcards.security.interaction.JwtResponse;
import com.example.bankcards.security.interaction.LoginRequest;
import com.example.bankcards.service.AuthenticationService;
import com.example.bankcards.util.JwtAuthUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/authentification")
public class AuthentificationController {

    private final AuthenticationService service;

    @PostMapping("/signIn")
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = service.authentication(loginRequest.getUsername(), loginRequest.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtAuthUtils.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

}
