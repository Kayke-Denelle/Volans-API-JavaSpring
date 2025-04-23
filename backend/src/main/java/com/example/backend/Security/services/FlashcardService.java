package com.example.backend.Security.services;


import java.util.List;

import com.example.backend.DTO.FlashcardDTO;


public interface FlashcardService {
    FlashcardDTO criarFlashcard(FlashcardDTO flashcardDTO);
    List<FlashcardDTO> listarPorBaralhoId(String baralhoId);
}