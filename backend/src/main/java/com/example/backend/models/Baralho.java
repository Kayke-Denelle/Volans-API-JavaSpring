package com.example.backend.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "baralhos")
public class Baralho {
    @Id
    private String id;
    private String Nome;
    private String descricao;
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    private String usuarioId; 
    private List<String> flashcardIds; 

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String Nome) {
        this.Nome = Nome;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<String> getFlashcardIds() {
        return flashcardIds;
    }

    public void setFlashcardIds(List<String> flashcardIds) {
        this.flashcardIds = flashcardIds;
    }
}