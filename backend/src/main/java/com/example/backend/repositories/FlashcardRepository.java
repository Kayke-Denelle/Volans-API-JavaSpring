package com.example.backend.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.backend.models.Flashcard;

public interface FlashcardRepository extends MongoRepository<Flashcard, String> {
    List<Flashcard> findByBaralhoId(String baralhoId);

    @Query("SELECT f.creationDate, COUNT(f) FROM Flashcard f GROUP BY f.creationDate ORDER BY f.creationDate")
    List<Object[]> findFlashcardsByCreationDate();
}
