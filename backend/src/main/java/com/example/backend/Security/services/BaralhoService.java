package com.example.backend.Security.services;

import java.util.List;

import com.example.backend.DTO.BaralhoDTO;

public interface BaralhoService {
    BaralhoDTO criarBaralho(BaralhoDTO baralhoDTO, String token);
    List<BaralhoDTO> listarBaralhosDoUsuario(String token);

    BaralhoDTO editarBaralho(String id, BaralhoDTO baralhoDTO, String token);
    void excluirBaralho(String id, String token);

    int contarBaralhosDoUsuario(String token);
    int contarFlashcardsDoUsuario(String token);
}
