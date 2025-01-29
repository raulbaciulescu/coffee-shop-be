package com.raulb.coffee_shop_be.controller;

import com.raulb.coffee_shop_be.dto.AuthenticationRequest;
import com.raulb.coffee_shop_be.dto.AuthenticationResponse;
import com.raulb.coffee_shop_be.dto.RegisterRequest;
import com.raulb.coffee_shop_be.service.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {
    private final AuthenticationServiceImpl service;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(service.authenticate(authenticationRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(service.register(registerRequest));
    }
}
