package com.api.usuario_post.model;

import com.api.usuario_post.event.ElasticEvent;
import com.api.usuario_post.event.PostEvent;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.lang.reflect.Type;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Node("User")
public class User {
    @Id @GeneratedValue
    private Long id;

    private Long userId;

    private String nomeCompleto;

    private String email;

    private String tag;

    private String senha;

    private List<PostEvent> colecaoSalvos = new ArrayList<>();

    private Curso curso;

    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    private Set<User> followers = new HashSet<>();

    @Relationship(type = "HAS_POST", direction = Relationship.Direction.OUTGOING)
    private List<Post> listPosts = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }

    public void follow(User user) {
        if (followers.contains(user)) {
            throw new IllegalStateException("Você já está seguindo esse usuário");
        }
        followers.add(user);
    }

    private String descricao;

    private String fotoPerfil = "https://storageimagesmoots.blob.core.windows.net/artifact-image-container/68a77764-1c2e-4bc4-8d6b-c280ac593970.png";

    private String fotoCapa = "https://storageimagesmoots.blob.core.windows.net/artifact-image-container/2442999d-d56d-4a55-994b-91f9286a0ef0.jpg";

    private List<String > roles = new ArrayList<>();

    public boolean moderador;
}