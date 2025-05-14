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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.DTO.FlashcardDTO;
import com.example.backend.Security.services.FlashcardService;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {

    @Autowired
    private FlashcardService flashcardService;

    @GetMapping
    public ResponseEntity<List<FlashcardDTO>> listar(@RequestParam String baralhoId) {
        return ResponseEntity.ok(flashcardService.listarPorBaralhoId(baralhoId));
    }

    @PostMapping
    public ResponseEntity<FlashcardDTO> criar(@RequestBody FlashcardDTO flashcardDTO) {
        return ResponseEntity.ok(flashcardService.criarFlashcard(flashcardDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlashcardDTO> editar(@PathVariable String id,
                                               @RequestBody FlashcardDTO flashcardDTO,
                                               @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(flashcardService.editarFlashcard(id, flashcardDTO, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id,
                                        @RequestHeader("Authorization") String token) {
        flashcardService.excluirFlashcard(id, token);
        return ResponseEntity.noContent().build();
    }
}
