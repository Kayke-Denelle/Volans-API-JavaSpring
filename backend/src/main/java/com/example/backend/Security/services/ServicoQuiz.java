package com.example.backend.Security.services;

import com.example.backend.models.Flashcard;
import com.example.backend.models.QuestaoQuiz;
import com.example.backend.repositories.FlashcardRepository;
import com.example.backend.repositories.QuizRepository;
import com.example.backend.DTO.RequisicaoQuiz;
import com.example.backend.DTO.ResultadoQuiz;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ServicoQuiz {
    private final QuizRepository quizRepo;
    private final FlashcardRepository flashcardRepo;
    private final ServicoOpenAI servicoOpenAI;

    public ServicoQuiz(QuizRepository quizRepo,
                     FlashcardRepository flashcardRepo,
                     ServicoOpenAI servicoOpenAI) {
        this.quizRepo = quizRepo;
        this.flashcardRepo = flashcardRepo;
        this.servicoOpenAI = servicoOpenAI;
    }

    public QuestaoQuiz gerarQuiz(String baralhoId) {
        // 1. Obter flashcards do baralho
        List<Flashcard> flashcards = flashcardRepo.findByBaralhoId(baralhoId);
        if (flashcards.isEmpty()) {
            throw new RuntimeException("Nenhum flashcard encontrado para o baralho: " + baralhoId);
        }

        // 2. Selecionar flashcard aleatório
        Flashcard card = flashcards.get(new Random().nextInt(flashcards.size()));

        // 3. Gerar variações usando OpenAI
        String pergunta = servicoOpenAI.gerarVariacaoPergunta(card.getPergunta());
        String resposta = servicoOpenAI.gerarVariacaoResposta(card.getResposta());

        // 4. Gerar alternativas
        List<String> alternativas = new ArrayList<>();
        alternativas.add(resposta); // Adiciona a resposta correta
        
        // Gera 3 distratores plausíveis
        List<String> distratores = servicoOpenAI.gerarDistratores(pergunta, resposta, 3);
        alternativas.addAll(distratores);
        
        // Embaralha as alternativas
        Collections.shuffle(alternativas);

        // 5. Criar e salvar o quiz
        QuestaoQuiz quiz = new QuestaoQuiz();
        quiz.setBaralhoId(baralhoId);
        quiz.setPergunta(pergunta);
        quiz.setRespostaCorreta(resposta);
        quiz.setAlternativas(alternativas);
        
        return quizRepo.save(quiz);
    }

    public ResultadoQuiz verificarResposta(RequisicaoQuiz requisicao) {
        // 1. Validar entrada
        if (requisicao.getQuizId() == null || requisicao.getAlternativaSelecionada() == null) {
            throw new IllegalArgumentException("Dados da requisição inválidos");
        }

        // 2. Buscar quiz
        QuestaoQuiz quiz = quizRepo.findById(requisicao.getQuizId())
            .orElseThrow(() -> new RuntimeException("Quiz não encontrado com ID: " + requisicao.getQuizId()));

        // 3. Verificar resposta
        boolean acertou = quiz.getRespostaCorreta().equalsIgnoreCase(requisicao.getAlternativaSelecionada());

        // 4. Remover quiz após resposta
        quizRepo.deleteById(requisicao.getQuizId());

        // 5. Gerar explicação
        String explicacao = servicoOpenAI.gerarExplicacao(
            quiz.getPergunta(),
            quiz.getRespostaCorreta(),
            requisicao.getAlternativaSelecionada(),
            acertou
        );

        return new ResultadoQuiz(acertou, quiz.getRespostaCorreta(), explicacao);
    }
}