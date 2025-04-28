package com.example.backend.DTO;

public class ResultadoQuiz {
    private boolean correto;
    private String respostaCorreta;
    private String explicacao;

    public ResultadoQuiz(boolean correto, String respostaCorreta, String explicacao) {
        this.correto = correto;
        this.respostaCorreta = respostaCorreta;
        this.explicacao = explicacao;
    }

    public boolean isCorreto() {
        return correto;
    }

    public void setCorreto(boolean correto) {
        this.correto = correto;
    }

    public String getRespostaCorreta() {
        return respostaCorreta;
    }

    public void setRespostaCorreta(String respostaCorreta) {
        this.respostaCorreta = respostaCorreta;
    }

    public String getExplicacao() {
        return explicacao;
    }

    public void setExplicacao(String explicacao) {
        this.explicacao = explicacao;
    }
}
