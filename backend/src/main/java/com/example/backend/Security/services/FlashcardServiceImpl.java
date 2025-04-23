package com.example.backend.Security.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.DTO.FlashcardDTO;
import com.example.backend.models.Flashcard;
import com.example.backend.repositories.FlashcardRepository;

@Service
public class FlashcardServiceImpl implements FlashcardService {

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Override
    public FlashcardDTO criarFlashcard(FlashcardDTO dto) {
        Flashcard flashcard = new Flashcard();
        flashcard.setPergunta(dto.getPergunta());
        flashcard.setResposta(dto.getResposta());
        flashcard.setBaralhoId(dto.getBaralhoId());
        Flashcard salvo = flashcardRepository.save(flashcard);
        return new FlashcardDTO(salvo.getId(), salvo.getPergunta(), salvo.getResposta(), salvo.getBaralhoId());
    }

    @Override
    public List<FlashcardDTO> listarPorBaralhoId(String baralhoId) {
        return flashcardRepository.findByBaralhoId(baralhoId)
                .stream()
                .map(f -> new FlashcardDTO(f.getId(), f.getPergunta(), f.getResposta(), f.getBaralhoId()))
                .collect(Collectors.toList());
    }
}
