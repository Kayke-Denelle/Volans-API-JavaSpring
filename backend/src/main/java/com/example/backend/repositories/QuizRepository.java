package com.example.backend.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.backend.models.QuestaoQuiz;

public interface QuizRepository extends MongoRepository<QuestaoQuiz, String> {
    void deleteByBaralhoId(String baralhoId);
    List<QuestaoQuiz> findByBaralhoId(String baralhoId);
     void deleteById(String quizId);
}