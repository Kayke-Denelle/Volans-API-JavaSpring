package com.example.backend.Security.JWT;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String chaveSecreta;

    @Value("${jwt.expiration-ms}")
    private long tempoExpiracao;

    public String gerarToken(String usuarioId) {
        return Jwts.builder()
                .setSubject(usuarioId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tempoExpiracao))
                .signWith(SignatureAlgorithm.HS512, chaveSecreta.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public String extrairUsuarioId(String token) {
        token = token.replace("Bearer ", "");
        Claims claims = Jwts.parser()
                .setSigningKey(chaveSecreta.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
