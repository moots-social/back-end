package com.api.usuario_post.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Node("POST")
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    private Long postId;

    private String userId;

    private String nomeCompleto;

    private String tag;

    private String fotoPerfil;

    private String texto;

    private List<String> listImagens;

    private Integer contadorLike = 0;

    private Integer contadorDeslike = 0;

    @JsonManagedReference
    @Relationship(type = "HAS_COMMENT", direction = Relationship.Direction.OUTGOING)
    private List<Comentario> comentarioList;


}
