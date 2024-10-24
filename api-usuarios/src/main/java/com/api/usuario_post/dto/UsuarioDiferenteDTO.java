package com.api.usuario_post.dto;

import com.api.usuario_post.event.ElasticEvent;
import com.api.usuario_post.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDiferenteDTO {
    private Long id;
    private Long userId;
    private String nomeCompleto;
    private String tag;
    private String descricao;
    private String curso;
    private Set<User> followers;
    private String fotoCapa;
    private String fotoPerfil;
    private List<ElasticEvent> listPosts;
}
