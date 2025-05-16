package com.example.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.DTO.RequisicaoQuiz;
import com.example.backend.DTO.RespostaUsuarioDTO;
import com.example.backend.DTO.ResultadoFinalDTO;
import com.example.backend.DTO.ResultadoQuiz;
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

    @PostMapping("/verificar")
    public ResponseEntity<ResultadoQuiz> verificarResposta(@RequestBody RequisicaoQuiz requisicao) {
        return ResponseEntity.ok(servicoQuiz.verificarResposta(requisicao));
    }

    @PostMapping("/avaliar")
public ResponseEntity<ResultadoFinalDTO> avaliarTodos(@RequestBody List<RespostaUsuarioDTO> respostas) {
    return ResponseEntity.ok(servicoQuiz.avaliarRespostas(respostas));
}

}