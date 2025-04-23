package com.example.backend.Security.services;

import java.util.List;

import com.example.backend.DTO.BaralhoDTO;

public interface BaralhoService {
    BaralhoDTO criarBaralho(BaralhoDTO baralhoDTO, String token);
    List<BaralhoDTO> listarBaralhosDoUsuario(String token);
}
