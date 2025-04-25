package com.example.backend.Security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.DTO.AdminDashboardDTO;
import com.example.backend.repositories.BaralhoRepository;
import com.example.backend.repositories.FlashcardRepository;
import com.example.backend.repositories.UsuariosRepository;

@Service
public class AdminService {

    @Autowired
    private UsuariosRepository usuarioRepository;

    @Autowired
    private BaralhoRepository baralhoRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    public AdminDashboardDTO getEstatisticas() {
        AdminDashboardDTO dto = new AdminDashboardDTO();
        dto.setTotalUsuarios(usuarioRepository.count());
        dto.setTotalBaralhos(baralhoRepository.count());
        dto.setTotalFlashcards(flashcardRepository.count());
        return dto;
    }
}
