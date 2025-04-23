package com.example.backend.DTO;

import java.time.LocalDateTime;

public class FlashcardDTO {
    private String id;
    private String pergunta;
    private String resposta;
    private String baralhoId;

    public FlashcardDTO() {
    }

    public FlashcardDTO(String id, String pergunta, String resposta, String baralhoId) {
        this.baralhoId = baralhoId;
        this.id = id;
        this.pergunta = pergunta;
        this.resposta = resposta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public String getBaralhoId() {
        return baralhoId;
    }

    public void setBaralhoId(String baralhoId) {
        this.baralhoId = baralhoId;
    }

    
}