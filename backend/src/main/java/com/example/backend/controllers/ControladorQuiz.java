package com.example.backend.controllers;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.backend.DTO.RespostaUsuarioDTO;
import com.example.backend.DTO.ResultadoFinalDTO;
import com.example.backend.Security.services.ServicoQuiz;
import com.example.backend.models.QuestaoQuiz;
import com.example.backend.models.QuizRegistro;
import com.example.backend.models.Baralho;
import com.example.backend.repositories.BaralhoRepository;
import com.example.backend.repositories.QuizRegistroRepository;

@RestController
@RequestMapping("/api/quizzes")
public class ControladorQuiz {

    private final ServicoQuiz servicoQuiz;
    private final QuizRegistroRepository quizRegistroRepository;
    private final BaralhoRepository baralhoRepository;

    public ControladorQuiz(ServicoQuiz servicoQuiz,
                           QuizRegistroRepository quizRegistroRepository,
                           BaralhoRepository baralhoRepository) {
        this.servicoQuiz = servicoQuiz;
        this.quizRegistroRepository = quizRegistroRepository;
        this.baralhoRepository = baralhoRepository;
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

    @GetMapping("/{quizId}")
    public ResponseEntity<QuestaoQuiz> getQuizPorId(@PathVariable String quizId) {
        return ResponseEntity.ok(servicoQuiz.buscarQuizPorId(quizId));
    }

    @DeleteMapping("/baralho/{baralhoId}")
    public ResponseEntity<Void> deletarQuizzesPorBaralho(@PathVariable String baralhoId) {
        servicoQuiz.deletarQuizPorBaralhoId(baralhoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deletarQuizPorId(@PathVariable String quizId) {
        servicoQuiz.deletarQuizPorId(quizId);
        return ResponseEntity.noContent().build();
    }

    // 🔥 Endpoint para retornar o total de revisões do usuário no mês atual
    @GetMapping("/historico/mensal")
public ResponseEntity<List<Map<String, Object>>> getHistoricoMensal() {
    String usuarioId = getUsuarioLogado();

    List<String> baralhoIds = baralhoRepository.findByUsuarioId(usuarioId)
            .stream()
            .map(Baralho::getId)
            .collect(Collectors.toList());

    LocalDateTime inicioDoMes = LocalDateTime.now()
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

    LocalDateTime agora = LocalDateTime.now();

    List<QuizRegistro> registros = quizRegistroRepository
            .findByBaralhoIdInAndDataCriacaoBetween(baralhoIds, inicioDoMes, agora);

    // 🔥 Agrupar por dia
    Map<String, Long> agrupadoPorDia = registros.stream()
            .collect(Collectors.groupingBy(
                    r -> r.getDataCriacao().toLocalDate().toString(), // Formato "2025-06-10"
                    Collectors.counting()
            ));

    // 🔧 Converter para lista de objetos (pode ser um DTO também)
    List<Map<String, Object>> resultado = agrupadoPorDia.entrySet().stream()
            .map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("data", e.getKey());
                map.put("quantidade", e.getValue());
                return map;
            })
            .sorted(Comparator.comparing(m -> (String) m.get("data"))) // 🔥 Ordenar por data
            .collect(Collectors.toList());

    return ResponseEntity.ok(resultado);
}


    private String getUsuarioLogado() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
