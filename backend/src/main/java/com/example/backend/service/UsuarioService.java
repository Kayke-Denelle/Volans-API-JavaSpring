package com.example.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.DTO.RegisterRequest;
import com.example.backend.models.Usuarios;
import com.example.backend.repositories.UsuariosRepository;

@Service
public class UsuarioService {

    private final UsuariosRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Injeção de dependência via construtor
    public UsuarioService(UsuariosRepository userRepository, 
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Usuarios createUser(RegisterRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username já está em uso");
        }
        
        if (existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        Usuarios user = new Usuarios();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        return userRepository.save(user);
    }
}