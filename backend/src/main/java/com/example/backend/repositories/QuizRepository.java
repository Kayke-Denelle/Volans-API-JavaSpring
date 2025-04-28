package com.example.backend.repositories;

import com.example.backend.models.QuestaoQuiz;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizRepository extends MongoRepository<QuestaoQuiz, String> {
    void deleteByBaralhoId(String baralhoId);
}