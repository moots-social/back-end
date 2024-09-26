package com.api.usuario_post.security;

import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JWTCreator {
    public static final String HEADER_AUTHORIZATION = "Authorization"; // Cabeçalho usado para o token JWT
    public static final String ROLES_AUTHORITIES = "authorities"; // Chave usada para armazenar as roles no token
    public static final String USER_ID = "user_id";


    // Metodo para criar um token JWT
    public static String create(String prefix, String key, JWTObject jwtObject) {
        // Cria o token JWT com os dados do JWTObject e assina com a chave secreta
        String token = Jwts.builder()
                .setSubject(jwtObject.getSubject()) // Define o subject (nome do usuário)
                .setIssuedAt(jwtObject.getIssuedAt()) // Define a data de emissão
                .setExpiration(jwtObject.getExpiration()) // Define a data de expiração
                .claim(ROLES_AUTHORITIES, checkRoles(jwtObject.getRoles())) // Adiciona as roles ao token
                .claim(USER_ID, jwtObject.getUserId()) // Aqui assume-se que o userId é Long
                .signWith(SignatureAlgorithm.HS512, key) // Assina o token com a chave secreta
                .compact();
        return prefix + " " + token; // Retorna o token com o prefixo (ex: "Bearer token...")
    }

    // Metodo para validar e extrair informações de um token JWT
    public static JWTObject validar(String token, String prefix, String key) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException {
        JWTObject object = new JWTObject();
        token = token.replace(prefix, ""); // Remove o prefixo do token
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody(); // Valida o token e extrai as claims

        object.setSubject(claims.getSubject()); // Define o subject (nome do usuário)
        object.setExpiration(claims.getExpiration()); // Define a data de expiração
        object.setIssuedAt(claims.getIssuedAt()); // Define a data de emissão
        object.setRoles((List<String>) claims.get(ROLES_AUTHORITIES)); // Define as roles

        // Verifique o tipo do user_id no token. Ajuste o tipo de cast conforme necessário.
        Object userIdClaim = claims.get(USER_ID);
        if (userIdClaim instanceof Integer) {
            object.setUserId(((Integer) userIdClaim).longValue());
        } else if (userIdClaim instanceof Long) {
            object.setUserId((Long) userIdClaim);
        } else {
            throw new IllegalArgumentException("Tipo de user_id inválido no token.");
        }

        return object;
    }

    // Metodo auxiliar para adicionar o prefixo "ROLE_" às roles
    private static List<String> checkRoles(List<String> roles) {
        return roles.stream()
                .map(s -> "ROLE_".concat(s.replaceAll("ROLE_", "")))
                .collect(Collectors.toList());
    }
}
