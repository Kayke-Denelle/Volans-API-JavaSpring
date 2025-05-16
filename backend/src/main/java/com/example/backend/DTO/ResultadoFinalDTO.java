package com.example.backend.DTO;

public class ResultadoFinalDTO {
    private int total;
    private int acertos;

    public ResultadoFinalDTO(int total, int acertos) {
        this.total = total;
        this.acertos = acertos;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAcertos() {
        return acertos;
    }

    public void setAcertos(int acertos) {
        this.acertos = acertos;
    }
}
