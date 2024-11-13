package com.moots.api_post.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Node("POST")
public class Post implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    private Long postId;

    @NonNull
    private String userId;

    @NonNull
    private String nomeCompleto;

    @NonNull
    private String tag;

    private String fotoPerfil;

    private String texto;

    private List<String> listImagens;

    @NonNull
    private Integer contadorLike = 0;

    @NonNull
    private Integer contadorDeslike = 0;

    private List<String> likeUsers = new ArrayList<>();

    @JsonManagedReference
    @Relationship(type = "HAS_COMMENT", direction = Relationship.Direction.OUTGOING)
    private List<Comentario> comentarioList;

}
