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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.DTO.FlashcardDTO;
import com.example.backend.Security.services.FlashcardService;
import com.example.backend.models.Flashcard;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {

    @Autowired
    private FlashcardService flashcardService;

    @PostMapping
    public ResponseEntity<FlashcardDTO> criar(@RequestBody FlashcardDTO dto) {
        return ResponseEntity.ok(flashcardService.criarFlashcard(dto));
    }

    @GetMapping("/baralho/{baralhoId}")
    public ResponseEntity<List<FlashcardDTO>> listarPorBaralho(@PathVariable String baralhoId) {
        return ResponseEntity.ok(flashcardService.listarPorBaralhoId(baralhoId));
    }
}
