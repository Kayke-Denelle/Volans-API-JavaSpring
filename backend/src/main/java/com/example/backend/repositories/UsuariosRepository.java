/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.example.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.backend.models.Usuarios;

/**
 *
 * @author kayke
 */
public interface UsuariosRepository extends MongoRepository<Usuarios, String> {
    Optional<Usuarios> findByUsername(String username);
    Optional<Usuarios> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    @Query("SELECT u.creationDate, COUNT(u) FROM Usuarios u GROUP BY u.creationDate ORDER BY u.creationDate")
    List<Object[]> findUsuariosByCreationDate();
}
