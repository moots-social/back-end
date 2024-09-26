package com.moots.api_busca.service;

import com.moots.api_busca.event.ElasticEvent;
import com.moots.api_busca.model.Post;
import com.moots.api_busca.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post salvarPostElastic(ElasticEvent elasticEvent){
        var post = new Post();
        post.setTag(elasticEvent.getTag());
        post.setListImagens(elasticEvent.getListImagens());
        post.setTexto(elasticEvent.getTexto());
        post.setContadorLike(elasticEvent.getContadorLike());
        post.setContadorDeslike(elasticEvent.getContadorDeslike());
        post.setNomeCompleto(elasticEvent.getNomeCompleto());
        post.setFotoPerfil(elasticEvent.getFotoPerfil());
        post.setUserId(elasticEvent.getUserId());
        post.setPostId(elasticEvent.getPostId());


        return postRepository.save(post);
    }

    public Post deletarPost(String postId){
        Post post = postRepository.findByPostId(postId);
        postRepository.deleteById(post.getId());
        return post;
    }

    public Iterable<Post> findAll(){
        return postRepository.findAll();
    }

    public List<Post> findByTextoOrTag(String query, int page){
        int size = 2;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Post> result = postRepository.findByTextoOrTag(query, query, pageRequest);
        return result;
    }

    public List<Post> findPostByUserId(String userId){
        List<Post> posts = postRepository.findByUserId(userId);
        return posts;
    }

}
