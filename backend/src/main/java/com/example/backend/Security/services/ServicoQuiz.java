package com.example.backend.Security.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backend.DTO.RequisicaoQuiz;
import com.example.backend.DTO.RespostaUsuarioDTO;
import com.example.backend.DTO.ResultadoFinalDTO;
import com.example.backend.DTO.ResultadoQuiz;
import com.example.backend.models.Flashcard;
import com.example.backend.models.QuestaoQuiz;
import com.example.backend.repositories.FlashcardRepository;
import com.example.backend.repositories.QuizRepository;

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

    public List<QuestaoQuiz> gerarQuiz(String baralhoId) {
    // 1. Obter todos os flashcards do baralho
    List<Flashcard> flashcards = flashcardRepo.findByBaralhoId(baralhoId);
    if (flashcards.isEmpty()) {
        throw new RuntimeException("Nenhum flashcard encontrado para o baralho: " + baralhoId);
    }

    List<QuestaoQuiz> quizzes = new ArrayList<>();

    // 2. Para cada flashcard, gerar uma questão
    for (Flashcard card : flashcards) {
        // 3. Gerar variações usando OpenAI
        String pergunta = servicoOpenAI.gerarVariacaoPergunta(card.getPergunta());
        String resposta = servicoOpenAI.gerarVariacaoResposta(card.getResposta());

        // 4. Gerar alternativas
        List<String> alternativas = new ArrayList<>();
        alternativas.add(resposta); // Resposta correta

        List<String> distratores = servicoOpenAI.gerarDistratores(pergunta, resposta, 3);
        alternativas.addAll(distratores);
        Collections.shuffle(alternativas); // Embaralhar opções

        // 5. Criar e salvar quiz
        QuestaoQuiz quiz = new QuestaoQuiz();
        quiz.setBaralhoId(baralhoId);
        quiz.setPergunta(pergunta);
        quiz.setRespostaCorreta(resposta);
        quiz.setAlternativas(alternativas);

        quizzes.add(quizRepo.save(quiz));
    }

    return quizzes;
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

    public ResultadoFinalDTO avaliarRespostas(List<RespostaUsuarioDTO> respostas) {
    int acertos = 0;

    for (RespostaUsuarioDTO resposta : respostas) {
        Optional<QuestaoQuiz> quizOpt = quizRepo.findById(resposta.getQuizId());

        if (quizOpt.isPresent()) {
            QuestaoQuiz quiz = quizOpt.get();

            if (quiz.getRespostaCorreta().equalsIgnoreCase(resposta.getRespostaSelecionada())) {
                acertos++;
            }

            quizRepo.deleteById(quiz.getId()); // Remove após responder
        }
    }

    return new ResultadoFinalDTO(respostas.size(), acertos);
}
}