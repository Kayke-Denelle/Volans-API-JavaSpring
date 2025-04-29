package com.example.backend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.DTO.AuthResponse;
import com.example.backend.DTO.LoginRequest;
import com.example.backend.DTO.RegisterRequest;
import com.example.backend.Security.JWT.JwtTokenProvider;
import com.example.backend.models.Usuarios;
import com.example.backend.service.UsuarioService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "https://volans-interface.vercel.app", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authenticationManager,
                        JwtTokenProvider tokenProvider,
                        UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    try {
        // 1. Cria o usuário
        Usuarios createdUser = usuarioService.createUser(request);

        // 2. Cria objeto de autenticação com as authorities
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            createdUser.getUsername(),
            request.getPassword(),
            createdUser.getAuthorities()
        );

        // Adicione um log para verificar se o authentication está correto
        logger.info("Authentication object: {}", authentication);

        // 3. Gera o token
        String jwt = tokenProvider.generateToken(authentication);
        if (jwt == null) {
            logger.error("Falha ao gerar token JWT para o usuário: {}", request.getUsername());
            throw new IllegalStateException("Falha ao gerar token JWT");
        }

        logger.info("Usuário registrado com sucesso: {}", request.getUsername());
        return ResponseEntity.ok(new AuthResponse(jwt, createdUser.getUsername()));

    } catch (IllegalArgumentException e) {
        logger.error("Erro no registro: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        logger.error("Erro inesperado durante o registro: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body("Erro durante o registro");
    }
}


@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    try {
        // 1. Autentica o usuário
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        // Adicione um log para verificar se a autenticação foi bem-sucedida
        logger.info("Autenticação bem-sucedida para o usuário: {}", request.getUsername());

        // 2. Gera o token
        String jwt = tokenProvider.generateToken(authentication);

        if (jwt == null) {
            logger.error("Falha ao gerar token JWT para o login do usuário: {}", request.getUsername());
            throw new IllegalStateException("Falha ao gerar token JWT");
        }

        return ResponseEntity.ok(new AuthResponse(jwt, request.getUsername()));

    } catch (AuthenticationException e) {
        logger.warn("Tentativa de login falhou para o usuário: {}", request.getUsername());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
               .body("Credenciais inválidas");
    } catch (Exception e) {
        logger.error("Erro inesperado durante o login: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body("Erro durante o login");
    }
}
}
