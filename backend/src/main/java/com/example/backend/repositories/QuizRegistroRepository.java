package com.example.backend.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.models.QuizRegistro;

@Repository
public interface QuizRegistroRepository extends MongoRepository<QuizRegistro, String> {
    List<QuizRegistro> findByBaralhoIdInAndDataCriacaoBetween(List<String> baralhoIds, LocalDateTime inicio, LocalDateTime fim);

}
