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

    private String numeroTelefone;

    private String tag;

    private String senha;

    private List<PostEvent> colecaoSalvos;

    private Curso curso;

    @Relationship(type = "HAVE", direction = Relationship.Direction.OUTGOING)
    private List<ElasticEvent> listPosts;

    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    private Set<User> followers = new HashSet<>();

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
            throw new IllegalStateException("Already following this user.");
        }
        followers.add(user);
    }

    private String descricao = "Seja bem-vindo(a) ao meu perfil.";

    private String fotoPerfil = "https://storageimagesmoots.blob.core.windows.net/artifact-image-container/68a77764-1c2e-4bc4-8d6b-c280ac593970.png";

    private String fotoCapa = "https://storageimagesmoots.blob.core.windows.net/artifact-image-container/23cdd609-94b2-43f1-9112-9ae85ef2abe5.jpg";

    private List<String > roles = new ArrayList<>();

    public boolean moderador;
}