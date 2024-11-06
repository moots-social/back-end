package com.api.usuario_post.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Node("COMENTARIO")
public class Comentario implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    private String texto;

    private String userId;

    private String nomeCompleto;

    private String fotoPerfil;

    private String tag;

    private Long postId;

    @JsonBackReference
    @Relationship(value = "HAS_COMMENT", direction = Relationship.Direction.INCOMING)
    private Post post;

}
