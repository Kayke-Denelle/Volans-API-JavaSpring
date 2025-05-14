package com.example.backend.Security.services;

import java.util.List;
import java.util.Optional;
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

    @Override
    public BaralhoDTO editarBaralho(String id, BaralhoDTO baralhoDTO, String token) {
        String usuarioId = jwtUtil.extrairUsuarioId(token); // nome corrigido
        Optional<Baralho> optionalBaralho = baralhoRepository.findById(id);

        if (optionalBaralho.isPresent()) {
            Baralho baralho = optionalBaralho.get();

            // Verifica se o baralho pertence ao usuário
            if (!baralho.getUsuarioId().equals(usuarioId)) {
                throw new RuntimeException("Você não tem permissão para editar este baralho.");
            }

            // Atualiza os dados do baralho
            baralho.setNome(baralhoDTO.getNome());
            baralho.setDescricao(baralhoDTO.getDescricao());
            Baralho salvo = baralhoRepository.save(baralho);

            return new BaralhoDTO(salvo.getId(), salvo.getNome(), salvo.getDescricao());
        } else {
            throw new RuntimeException("Baralho não encontrado.");
        }
    }

    @Override
    public void excluirBaralho(String id, String token) {
        String usuarioId = jwtUtil.extrairUsuarioId(token); // nome corrigido
        Optional<Baralho> optionalBaralho = baralhoRepository.findById(id);

        if (optionalBaralho.isPresent()) {
            Baralho baralho = optionalBaralho.get();

            // Verifica se o baralho pertence ao usuário
            if (!baralho.getUsuarioId().equals(usuarioId)) {
                throw new RuntimeException("Você não tem permissão para excluir este baralho.");
            }

            // Exclui o baralho
            baralhoRepository.delete(baralho);
        } else {
            throw new RuntimeException("Baralho não encontrado.");
        }
    }
}
