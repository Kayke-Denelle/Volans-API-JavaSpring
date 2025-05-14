package com.example.backend.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.backend.models.Baralho;

public interface BaralhoRepository extends MongoRepository<Baralho, String> {
    List<Baralho> findByUsuarioId(String usuarioId);
    
}
