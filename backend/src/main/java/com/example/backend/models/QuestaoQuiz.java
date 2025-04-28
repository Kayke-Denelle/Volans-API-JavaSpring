package com.example.backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "questoes_quiz")
public class QuestaoQuiz {
    @Id
    private String id;
    private String baralhoId;
    private String pergunta;
    private String respostaCorreta;
    private List<String> alternativas;
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
    public String getPergunta() {
        return pergunta;
    }
    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }
    public String getRespostaCorreta() {
        return respostaCorreta;
    }
    public void setRespostaCorreta(String respostaCorreta) {
        this.respostaCorreta = respostaCorreta;
    }
    public List<String> getAlternativas() {
        return alternativas;
    }
    public void setAlternativas(List<String> alternativas) {
        this.alternativas = alternativas;
    }
    
    // Getters e Setters
}
