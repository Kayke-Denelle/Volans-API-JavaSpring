/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.example.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author kayke
 */
@RestController
@RequestMapping("/api/test")
public class testController {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @GetMapping("/connection")
    public String testConnection() {
        try {
            mongoTemplate.executeCommand("{ ping: 1 }");
            return "Conexão com MongoDB Atlas estabelecida com sucesso!";
        } catch (Exception e) {
            return "Falha na conexão: " + e.getMessage();
        }
    }
}