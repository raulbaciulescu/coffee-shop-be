package com.raulb.coffee_shop_be.service;


import com.raulb.coffee_shop_be.domain.Role;
import com.raulb.coffee_shop_be.domain.User;
import com.raulb.coffee_shop_be.dto.AuthenticationRequest;
import com.raulb.coffee_shop_be.dto.AuthenticationResponse;
import com.raulb.coffee_shop_be.dto.RegisterRequest;
import com.raulb.coffee_shop_be.exception.ResourceNotFoundException;
import com.raulb.coffee_shop_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtServiceImpl;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var user = new User(
                registerRequest.firstName(),
                registerRequest.lastName(),
                registerRequest.username(),
                passwordEncoder.encode(registerRequest.password()),
                Role.ADMIN
        );

        userRepository.save(user);
        var jwtToken = jwtServiceImpl.generateToken(user);
        return new AuthenticationResponse(jwtToken, registerRequest.firstName());
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.username(),
                        authenticationRequest.password()
                )
        );
        var user = userRepository.findByUsername(authenticationRequest.username())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        var jwtToken = jwtServiceImpl.generateToken(user);
        return new AuthenticationResponse(jwtToken, user.getUsername());
    }
}
