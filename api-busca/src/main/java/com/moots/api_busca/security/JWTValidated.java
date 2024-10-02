package com.moots.api_busca.security;

import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JWTValidated {
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String ROLES_AUTHORITIES = "authorities"; // Chave usada para armazenar as roles no token
    public static final String USER_ID = "user_id";

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
