package com.api.usuario_post.model;

import com.api.usuario_post.event.ColecaoPostEvent;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

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

    private List<ColecaoPostEvent> colecaoSalvos;

    private Curso curso;

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

    private String descricao;

    private String fotoPerfil;

    private String fotoCapa;

    private List<String > roles = new ArrayList<>();

    public boolean moderador;
}