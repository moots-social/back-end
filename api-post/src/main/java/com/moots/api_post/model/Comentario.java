package com.moots.api_post.model;

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

    @NonNull
    private String texto;

    @NonNull
    private String userId;

    @NonNull
    private String nomeCompleto;

    @NonNull
    private String fotoPerfil;

    @NonNull
    private String tag;

    @NonNull
    private Long postId;

    @JsonBackReference
    @Relationship(value = "HAS_COMMENT", direction = Relationship.Direction.INCOMING)
    private Post post;


}
