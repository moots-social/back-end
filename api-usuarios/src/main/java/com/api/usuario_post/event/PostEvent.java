package com.api.usuario_post.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private Long userId;

    private Long postId;

    private String texto;

    private List<String> listImagens;

    private String nomeCompleto;

    private String tag;

    private String fotoPerfil;
}