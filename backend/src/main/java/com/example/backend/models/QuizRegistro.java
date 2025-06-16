package com.example.backend.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quiz_registro")
public class QuizRegistro {

    @Id
    private String id;
    private String baralhoId;
    private LocalDateTime dataCriacao;

    public QuizRegistro() {
        this.dataCriacao = LocalDateTime.now();
    }

    public QuizRegistro(String baralhoId, LocalDateTime dataCriacao) {
        this.baralhoId = baralhoId;
        this.dataCriacao = LocalDateTime.now();
    }

    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBaralhoId() {
        return baralhoId;
    }

    public void setBaralhoId(String baralhoId) {
        this.baralhoId = baralhoId;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
