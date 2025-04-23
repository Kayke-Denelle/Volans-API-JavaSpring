package com.example.backend.Security.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.DTO.BaralhoDTO;
import com.example.backend.Security.JWT.JwtUtil;
import com.example.backend.models.Baralho;
import com.example.backend.repositories.BaralhoRepository;

@Service
public class BaralhoServiceImpl implements BaralhoService {

    @Autowired
    private BaralhoRepository baralhoRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public BaralhoDTO criarBaralho(BaralhoDTO baralhoDTO, String token) {
        String usuarioId = jwtUtil.extrairUsuarioId(token); // nome corrigido
        Baralho baralho = new Baralho();
        baralho.setNome(baralhoDTO.getNome());
        baralho.setDescricao(baralhoDTO.getDescricao());
        baralho.setUsuarioId(usuarioId);
        Baralho salvo = baralhoRepository.save(baralho);
        return new BaralhoDTO(salvo.getId(), salvo.getNome(), salvo.getDescricao());
    }

    @Override
    public List<BaralhoDTO> listarBaralhosDoUsuario(String token) {
        String usuarioId = jwtUtil.extrairUsuarioId(token); // nome corrigido
        return baralhoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(b -> new BaralhoDTO(b.getId(), b.getNome(), b.getDescricao()))
                .collect(Collectors.toList());
    }
}
