package com.example.backend.Security.services;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.DTO.RequisicaoQuiz;
import com.example.backend.DTO.RespostaUsuarioDTO;
import com.example.backend.DTO.ResultadoFinalDTO;
import com.example.backend.DTO.ResultadoQuiz;
import com.example.backend.Exception.ResourceNotFoundException;
import com.example.backend.models.Flashcard;
import com.example.backend.models.QuestaoQuiz;
import com.example.backend.models.QuizRegistro;
import com.example.backend.repositories.FlashcardRepository;
import com.example.backend.repositories.QuizRegistroRepository;
import com.example.backend.repositories.QuizRepository;

@Service
public class ServicoQuiz {

    private final QuizRepository quizRepo;
    private final FlashcardRepository flashcardRepo;
    private final ServicoOpenAI servicoOpenAI;
    private final QuizRegistroRepository quizRegistroRepo;

    public ServicoQuiz(QuizRepository quizRepo,
                        FlashcardRepository flashcardRepo,
                        ServicoOpenAI servicoOpenAI,
                        QuizRegistroRepository quizRegistroRepo) {
        this.quizRepo = quizRepo;
        this.flashcardRepo = flashcardRepo;
        this.servicoOpenAI = servicoOpenAI;
        this.quizRegistroRepo = quizRegistroRepo;
    }

    @Transactional(readOnly = true)
    public List<QuestaoQuiz> buscarQuizzesPorBaralho(String baralhoId) {
        List<QuestaoQuiz> quizzes = quizRepo.findByBaralhoId(baralhoId);

        if (quizzes.isEmpty()) {
            return gerarQuiz(baralhoId);
        }

        return quizzes;
    }

    @Transactional
    public List<QuestaoQuiz> gerarQuiz(String baralhoId) {
        List<Flashcard> flashcards = flashcardRepo.findByBaralhoId(baralhoId);

        if (flashcards.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum flashcard encontrado para o baralho: " + baralhoId);
        }

        List<QuestaoQuiz> questoes = flashcards.stream()
                .map(card -> criarQuestaoQuiz(card, baralhoId))
                .collect(Collectors.toList());

        
        QuizRegistro registro = new QuizRegistro(baralhoId, LocalDateTime.now());
        quizRegistroRepo.save(registro);

        return questoes;
    }

    private QuestaoQuiz criarQuestaoQuiz(Flashcard card, String baralhoId) {
        String pergunta = servicoOpenAI.gerarVariacaoPergunta(card.getPergunta());
        String resposta = servicoOpenAI.gerarVariacaoResposta(card.getResposta());

        List<String> alternativas = new ArrayList<>();
        alternativas.add(resposta);
        alternativas.addAll(servicoOpenAI.gerarDistratores(pergunta, resposta, 3));
        Collections.shuffle(alternativas);

        QuestaoQuiz quiz = new QuestaoQuiz();
        quiz.setBaralhoId(baralhoId);
        quiz.setPergunta(pergunta);
        quiz.setRespostaCorreta(resposta);
        quiz.setAlternativas(alternativas);

        return quizRepo.save(quiz);
    }

    @Transactional(readOnly = true)
    public QuestaoQuiz buscarQuizPorId(String quizId) {
        return quizRepo.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz não encontrado com id: " + quizId));
    }

    @Transactional
    public ResultadoQuiz verificarResposta(RequisicaoQuiz requisicao) {
        if (requisicao.getQuizId() == null || requisicao.getAlternativaSelecionada() == null) {
            throw new IllegalArgumentException("Dados da requisição inválidos");
        }

        QuestaoQuiz quiz = buscarQuizPorId(requisicao.getQuizId());
        boolean acertou = quiz.getRespostaCorreta().equalsIgnoreCase(requisicao.getAlternativaSelecionada());

        String explicacao = servicoOpenAI.gerarExplicacao(
                quiz.getPergunta(),
                quiz.getRespostaCorreta(),
                requisicao.getAlternativaSelecionada(),
                acertou
        );

        quizRepo.deleteById(requisicao.getQuizId());

        return new ResultadoQuiz(acertou, quiz.getRespostaCorreta(), explicacao);
    }

    @Transactional
    public ResultadoFinalDTO avaliarRespostas(List<RespostaUsuarioDTO> respostas) {
        if (respostas == null || respostas.isEmpty()) {
            throw new IllegalArgumentException("Lista de respostas não pode ser vazia");
        }

        int acertos = 0;
        List<String> quizzesRespondidos = new ArrayList<>();

        for (RespostaUsuarioDTO resposta : respostas) {
            Optional<QuestaoQuiz> quizOpt = quizRepo.findById(resposta.getQuizId());

            if (quizOpt.isPresent()) {
                QuestaoQuiz quiz = quizOpt.get();

                if (quiz.getRespostaCorreta().equalsIgnoreCase(resposta.getRespostaSelecionada())) {
                    acertos++;
                }

                quizzesRespondidos.add(quiz.getId());
            }
        }

        if (!quizzesRespondidos.isEmpty()) {
            quizRepo.deleteAllById(quizzesRespondidos);
        }

        return new ResultadoFinalDTO(respostas.size(), acertos);
    }

    @Transactional
    public void deletarQuizPorBaralhoId(String baralhoId) {
        quizRepo.deleteByBaralhoId(baralhoId);
    }

    @Transactional
    public void deletarQuizPorId(String quizId) {
        quizRepo.deleteById(quizId);
    }


}
