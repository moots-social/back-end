package com.api.usuario_post.repository;

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

    User findByTag(String tag);

    @Query("MATCH (u:User)<-[:FOLLOWS]-(f:User) WHERE u.userId = $userId RETURN f")
    List<User> findFollowersByUserId(Long userId);

    @Query("MATCH (u:User)-[:FOLLOWS]->(f:User) WHERE u.userId = $userId RETURN f")
    List<User> findFollowingByUserId(Long userId);

    @Query("MATCH (p:User) WHERE p.userId = $userId " +
            "OPTIONAL MATCH (p)<-[:FOLLOWS]-(f:User) " +
            "RETURN p, collect(f) AS followers")
    User findOnlyUser(Long userId);

    Optional<User> findByUserId(Long userId);
}
