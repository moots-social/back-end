package com.api.usuario_post.repository;

import com.api.usuario_post.event.PostEvent;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostEventRepository extends Neo4jRepository<PostEvent, Long> {

    PostEvent findByPostId(Long postId);

    void deleteByPostId(Long postId);

}
