package com.moots.api_busca.repository;

import com.moots.api_busca.event.ElasticEvent;
import com.moots.api_busca.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends ElasticsearchRepository<Post, String> {

    Post findByPostId(String postId);

    List<Post> findByTextoOrTag(String texto, String tag, Pageable pageable); // 10 posts

    List<Post> findByUserId(String userId);
}
