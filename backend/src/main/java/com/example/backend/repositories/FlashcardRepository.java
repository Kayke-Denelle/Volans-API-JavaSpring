package com.example.backend.repositories;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.backend.models.Flashcard;

public interface FlashcardRepository extends MongoRepository<Flashcard, String> {
    List<Flashcard> findByBaralhoId(String baralhoId);
}
