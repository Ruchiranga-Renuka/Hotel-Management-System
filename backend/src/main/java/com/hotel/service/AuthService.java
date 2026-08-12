package com.hotel.service;

import com.hotel.dto.AuthRequest;
import com.hotel.dto.AuthResponse;
import com.hotel.model.User;
import com.hotel.model.enums.Role;
import com.hotel.repository.UserRepository;
import com.hotel.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return null;
        }

        User user = new User(request.getName(), request.getEmail(), passwordEncoder.encode(request.getPassword()), Role.GUEST);
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> new AuthResponse(jwtUtil.generateToken(user.getEmail(), user.getRole())))
                .orElse(null);
    }
}
