package com.example.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.models.Usuarios;
import com.example.backend.repositories.BaralhoRepository;
import com.example.backend.repositories.FlashcardRepository;
import com.example.backend.repositories.UsuariosRepository;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuariosRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

     @Autowired
    private BaralhoRepository baralhoRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    @PostMapping("/criar")
    public ResponseEntity<?> criarAdmin(@RequestBody Usuarios usuario) {
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Usuário já existe");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRoles(List.of("ROLE_ADMIN"));

        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Administrador criado com sucesso!");
    }

     @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        data.put("totalUsuarios", usuarioRepository.count());
        data.put("totalBaralhos", baralhoRepository.count());
        data.put("totalFlashcards", flashcardRepository.count());

        // Listas completas
    data.put("usuarios", usuarioRepository.findAll());
    data.put("baralhos", baralhoRepository.findAll());
    data.put("flashcards", flashcardRepository.findAll());

        return ResponseEntity.ok(data);
    }
}
