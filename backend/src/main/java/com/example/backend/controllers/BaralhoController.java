package com.example.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.DTO.BaralhoDTO;
import com.example.backend.Security.services.BaralhoService;

@RestController
@RequestMapping("/api/baralhos")
public class BaralhoController {

    @Autowired
    private BaralhoService baralhoService;

    @PostMapping
    public ResponseEntity<BaralhoDTO> criar(@RequestBody BaralhoDTO baralhoDTO,
                                            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(baralhoService.criarBaralho(baralhoDTO, token));
    }

    @GetMapping
    public ResponseEntity<List<BaralhoDTO>> listar(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(baralhoService.listarBaralhosDoUsuario(token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaralhoDTO> editar(@PathVariable  String id, 
                                             @RequestBody BaralhoDTO baralhoDTO,
                                             @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(baralhoService.editarBaralho(id, baralhoDTO, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable  String id,
                                        @RequestHeader("Authorization") String token) {
        baralhoService.excluirBaralho(id, token);
        return ResponseEntity.noContent().build();
    }
}
