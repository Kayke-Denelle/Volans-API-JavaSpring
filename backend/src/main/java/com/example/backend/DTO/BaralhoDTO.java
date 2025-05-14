package com.example.backend.DTO;

public class BaralhoDTO {
    private String id;
    private String Nome;
    private String descricao;


    public BaralhoDTO(String id, String Nome, String descricao ) {
        this.Nome = Nome;
        this.descricao = descricao;
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    

    public BaralhoDTO() {
    }

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


    
}
