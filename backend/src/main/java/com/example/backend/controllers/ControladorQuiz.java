package com.example.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.DTO.RespostaUsuarioDTO;
import com.example.backend.DTO.ResultadoFinalDTO;
import com.example.backend.Security.services.ServicoQuiz;
import com.example.backend.models.QuestaoQuiz;

@RestController
@RequestMapping("/api/quizzes")
public class ControladorQuiz {
    private final ServicoQuiz servicoQuiz;

    public ControladorQuiz(ServicoQuiz servicoQuiz) {
        this.servicoQuiz = servicoQuiz;
    }

    @PostMapping("/gerar/{baralhoId}")
    public ResponseEntity<List<QuestaoQuiz>> gerarQuiz(@PathVariable String baralhoId) {
        return ResponseEntity.ok(servicoQuiz.gerarQuiz(baralhoId));
    }

    @PostMapping("/avaliar")
    public ResponseEntity<ResultadoFinalDTO> avaliarTodos(@RequestBody List<RespostaUsuarioDTO> respostas) {
        return ResponseEntity.ok(servicoQuiz.avaliarRespostas(respostas));
    }

    @GetMapping("/baralho/{baralhoId}")
    public ResponseEntity<List<QuestaoQuiz>> getQuizzesPorBaralho(@PathVariable String baralhoId) {
        return ResponseEntity.ok(servicoQuiz.buscarQuizzesPorBaralho(baralhoId));
    }

    // Novo endpoint para buscar um quiz específico por ID
    @GetMapping("/{quizId}")
public ResponseEntity<QuestaoQuiz> getQuizPorId(@PathVariable String quizId) {
    return ResponseEntity.ok(servicoQuiz.buscarQuizPorId(quizId));
}
// Endpoint para deletar todos os quizzes de um baralho
    @DeleteMapping("/baralho/{baralhoId}")
    public ResponseEntity<Void> deletarQuizzesPorBaralho(@PathVariable String baralhoId) {
        servicoQuiz.deletarQuizPorBaralhoId(baralhoId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para deletar um quiz específico por ID
    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deletarQuizPorId(@PathVariable String quizId) {
        servicoQuiz.deletarQuizPorId(quizId);
        return ResponseEntity.noContent().build();
    }



}