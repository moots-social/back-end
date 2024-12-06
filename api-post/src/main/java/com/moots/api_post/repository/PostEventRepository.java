package com.moots.api_post.repository;

import com.moots.api_post.event.PostEvent;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostEventRepository extends Neo4jRepository<PostEvent, Long> {

    List<PostEvent> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
