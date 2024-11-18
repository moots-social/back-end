package com.api.usuario_post.repository;

import com.api.usuario_post.event.PostEvent;
import com.api.usuario_post.model.Post;
import com.api.usuario_post.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends Neo4jRepository <User, Long> {

    List<User> findByNomeCompleto(String username);

    User findByEmail(String email);

    @Query("MATCH (p:User) WHERE p.email = $email " +
            "OPTIONAL MATCH (p)<-[:FOLLOWS]-(f:User) " +
            "RETURN p, collect(f) AS followers")
    User findByOnlyEmail(String email);

    User findByTag(String tag);

    @Query("MATCH (u:User)<-[:FOLLOWS]-(f:User) WHERE u.userId = $userId RETURN f")
    List<User> findFollowersByUserId(Long userId);

    @Query("MATCH (u:User)-[:FOLLOWS]->(f:User) WHERE u.userId = $userId RETURN f")
    List<User> findFollowingByUserId(Long userId);

    @Query("MATCH (u:User)-[:HAS_POST]->(p:POST) WHERE u.userId = $userId RETURN p.postId as postId, p.nomeCompleto as nomeCompleto, p.tag as tag, p.fotoPerfil as fotoPerfil, p.texto as texto, p.listImagens as listImagens, p.contadorLike as contadorLike, p.contadorDeslike as contadorDeslike, p.comentarioList as comentarioList")
    List<Post> findPostsByUserId(Long userId);

    @Query("MATCH (u:User)-[:FOLLOWS]->(f:User)-[:HAS_POST]->(p:POST) WHERE u.userId = $userId RETURN p.postId as postId, p.nomeCompleto as nomeCompleto, p.tag as tag, p.fotoPerfil as fotoPerfil, p.texto as texto, p.listImagens as listImagens, p.contadorLike as contadorLike, p.contadorDeslike as contadorDeslike, p.comentarioList as comentarioList")
    List<Post> findPostAndComentarioByFollowers(Long userId);

    @Query("MATCH (p:User) WHERE p.userId = $userId " +
            "OPTIONAL MATCH (p)<-[:FOLLOWS]-(f:User) " +
            "RETURN p, collect(f) AS followers")
    User findOnlyUser(Long userId);

    Optional<User> findByUserId(Long userId);

//    PostEvent deletePostEventByPostId(Long postId);

}
