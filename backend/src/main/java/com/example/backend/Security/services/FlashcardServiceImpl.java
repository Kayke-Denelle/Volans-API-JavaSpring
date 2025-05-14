package com.example.backend.Security.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.DTO.FlashcardDTO;
import com.example.backend.Security.JWT.JwtUtil;
import com.example.backend.models.Flashcard;
import com.example.backend.repositories.FlashcardRepository;

@Service
public class FlashcardServiceImpl implements FlashcardService {

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private JwtUtil jwtUtils;

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

    @Override
    public FlashcardDTO editarFlashcard(String id, FlashcardDTO flashcardDTO, String token) {
        // Valida o token para garantir que o usuário autenticado é o correto
        String usuarioId = jwtUtils.extrairUsuarioId(token); // A extração do usuário do token
        Optional<Flashcard> optionalFlashcard = flashcardRepository.findById(id);

        if (optionalFlashcard.isPresent()) {
            Flashcard flashcard = optionalFlashcard.get();
            
            // Verifica se o flashcard pertence ao baralho do usuário autenticado
            if (!flashcard.getBaralhoId().equals(usuarioId)) {
                throw new RuntimeException("Você não tem permissão para editar este flashcard.");
            }

            // Atualiza o flashcard
            flashcard.setPergunta(flashcardDTO.getPergunta());
            flashcard.setResposta(flashcardDTO.getResposta());
            flashcard.setBaralhoId(flashcardDTO.getBaralhoId());
            Flashcard salvo = flashcardRepository.save(flashcard);

            return new FlashcardDTO(salvo.getId(), salvo.getPergunta(), salvo.getResposta(), salvo.getBaralhoId());
        } else {
            throw new RuntimeException("Flashcard não encontrado.");
        }
    }

    @Override
    public void excluirFlashcard(String id, String token) {
        // Valida o token para garantir que o usuário autenticado é o correto
        String usuarioId = jwtUtils.extrairUsuarioId(token); // A extração do usuário do token
        Optional<Flashcard> optionalFlashcard = flashcardRepository.findById(id);

        if (optionalFlashcard.isPresent()) {
            Flashcard flashcard = optionalFlashcard.get();

            // Verifica se o flashcard pertence ao baralho do usuário autenticado
            if (!flashcard.getBaralhoId().equals(usuarioId)) {
                throw new RuntimeException("Você não tem permissão para excluir este flashcard.");
            }

            // Exclui o flashcard
            flashcardRepository.delete(flashcard);
        } else {
            throw new RuntimeException("Flashcard não encontrado.");
        }
    }
}
