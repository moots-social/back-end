package com.moots.api_post.repository;

import com.moots.api_post.event.ElasticEvent;
import com.moots.api_post.model.Comentario;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends Neo4jRepository<Comentario, Long> {

    List<Comentario> findByUserId(String userId);

    void deleteByUserId(String userId);
}
