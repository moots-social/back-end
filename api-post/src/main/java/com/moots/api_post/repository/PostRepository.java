package com.moots.api_post.repository;

import com.moots.api_post.model.Post;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface PostRepository extends Neo4jRepository<Post, Long> {

    @Query("MATCH (p:POST {postId: $postId}) OPTIONAL MATCH (p)-[:HAS_COMMENT]->(c:COMENTARIO) WHERE c.postId = $postId DETACH DELETE p, c")
    Post deletarPostEComentarios(Long postId);


}
