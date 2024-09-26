package com.api.usuario_post.dto;

import com.api.usuario_post.model.Curso;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDTO {
    @NotBlank(message = "Seu nome completo é obrigatório")
    private String nomeCompleto;

    @NotBlank(message = "Seu email é obrigatório")
    private String email;

    @NotBlank(message = "Sua senha é obrigatório")
    private String senha;

    private String numeroTelefone;

    @NotBlank(message = "Sua tag é obrigatório")
    private String tag;

    private Curso curso;

    private String descricao;

    private String fotoPerfil;

    private String fotoCapa;

    @NotBlank(message = "Sua permissão é obrigatoria")
    private List<String> roles;

    public UserDTO(String nomeCompleto, String email, String senha, String numeroTelefone, String tag, Curso curso, String descricao, String fotoPerfil, String fotoCapa, List<String> roles) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senha = senha;
        this.numeroTelefone = numeroTelefone;
        this.tag = tag;
        this.curso = curso;
        this.descricao = descricao;
        this.fotoPerfil = fotoPerfil;
        this.fotoCapa = fotoCapa;
        this.roles = roles;
    }
}
