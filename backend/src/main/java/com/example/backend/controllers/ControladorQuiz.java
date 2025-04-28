package com.example.backend.controllers;

import com.example.backend.models.QuestaoQuiz;
import com.example.backend.Security.services.ServicoQuiz;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.DTO.RequisicaoQuiz;
import com.example.backend.DTO.ResultadoQuiz;

@RestController
@RequestMapping("/api/quizzes")
public class ControladorQuiz {
    private final ServicoQuiz servicoQuiz;

    public ControladorQuiz(ServicoQuiz servicoQuiz) {
        this.servicoQuiz = servicoQuiz;
    }

    @PostMapping("/gerar/{baralhoId}")
    public ResponseEntity<QuestaoQuiz> gerarQuiz(@PathVariable String baralhoId) {
        return ResponseEntity.ok(servicoQuiz.gerarQuiz(baralhoId));
    }

    @PostMapping("/verificar")
    public ResponseEntity<ResultadoQuiz> verificarResposta(@RequestBody RequisicaoQuiz requisicao) {
        return ResponseEntity.ok(servicoQuiz.verificarResposta(requisicao));
    }
}