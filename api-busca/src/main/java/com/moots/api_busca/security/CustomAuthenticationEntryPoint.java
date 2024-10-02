package com.moots.api_busca.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Código de status 403 Forbidden
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Cria a resposta JSON
        String errorMessage = "{\"error\": \"Acesso negado. Você não tem permissão para acessar este recurso.\"}";

        try (PrintWriter writer = response.getWriter()) {
            writer.write(errorMessage);
            writer.flush();
        }
    }
}
