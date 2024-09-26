package com.api.usuario_post.security;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class JWTObject {
    private String subject; // Nome do usuário
    private Date issuedAt; // Data de criação do token
    private Date expiration; // Data de expiração do token
    private List<String> roles; // Perfis de acesso do usuário
    private Long UserId;

    // Getters e Setters para as propriedades
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issueAt) {
        this.issuedAt = issueAt;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    // Sobrecarga para configurar as roles com um array de strings
    public void setRoles(String... roles){
        this.roles = Arrays.asList(roles);
    }

    public Long getUserId() {
        return UserId;
    }

    public void setUserId(Long UserId) {
        this.UserId = UserId;
    }
}


