package com.moots.api_busca.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JWTFilter extends OncePerRequestFilter {
    private static final String ERROR_MESSAGE = "{\"error\": \"Token inválido ou expirado.\"}";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Obtém o token do cabeçalho "Authorization"
        String token = request.getHeader(JWTValidated.HEADER_AUTHORIZATION);

        // Tenta validar e extrair as informações do token
        try {
            if(token != null && !token.isEmpty()) {
                // Valida e extrai os dados do token
                JWTObject tokenObject = JWTValidated.validar(token, SecurityConfig.PREFIX, SecurityConfig.KEY);

                // Converte as roles para SimpleGrantedAuthority
                List<SimpleGrantedAuthority> authorities = authorities(tokenObject.getRoles());

                // Cria o objeto de autenticação do Spring Security
                UsernamePasswordAuthenticationToken userToken =
                        new UsernamePasswordAuthenticationToken(
                                tokenObject.getUserId(),
                                null,
                                authorities);

                // Define o usuário autenticado no contexto de segurança
                SecurityContextHolder.getContext().setAuthentication(userToken);
            } else {
                // Limpa o contexto de segurança se o token não for válido
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response); // Continua o processamento da requisição
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8"); // Define o encoding para UTF-8
            response.getWriter().write(ERROR_MESSAGE);
            response.getWriter().flush(); // Garante que todos os dados sejam escritos
        }
    }

    // Converte as roles para o formato esperado pelo Spring Security
    private List<SimpleGrantedAuthority> authorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}