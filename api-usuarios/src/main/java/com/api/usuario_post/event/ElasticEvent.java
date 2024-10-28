package com.api.usuario_post.event;

import com.api.usuario_post.model.Curso;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElasticEvent implements Serializable {

    private String userId;

    private Long postId;

    private String nomeCompleto;

    private String tag;

    private String fotoPerfil;

    private String texto;

    private List<String> listImagens;

    private Integer contadorLike;

    private Integer contadorDeslike;

    private String curso;

}
