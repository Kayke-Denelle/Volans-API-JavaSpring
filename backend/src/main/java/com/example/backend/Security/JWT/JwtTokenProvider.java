package com.example.backend.Security.JWT;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
    
        private final SecretKey jwtSecret;
        private final long jwtExpirationMs;
    
        public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecretString,
                                @Value("${jwt.expiration-ms}") long jwtExpirationMs) {
            this.jwtSecret = Keys.hmacShaKeyFor(jwtSecretString.getBytes());
            this.jwtExpirationMs = jwtExpirationMs;
        }

        public String generateToken(Authentication authentication) {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        
            System.out.println("Gerando token para usuário: " + authentication.getName());
            System.out.println("Roles: " + roles);
        
            return Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim("roles", roles)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(jwtSecret, SignatureAlgorithm.HS512)
                    .compact();
        }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Logar o erro se necessário
            return false;
        }
    }

     public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        List<String> roles = getRolesFromToken(token);
        
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        
        return new UsernamePasswordAuthenticationToken(
            username,
            null,
            authorities
        );
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    
        Object roles = claims.get("roles");
        if (roles instanceof List) {
            return (List<String>) roles;
        }
        return List.of();
    }
}