package com.moots.api_busca.service;

import com.moots.api_busca.event.ElasticEvent;
import com.moots.api_busca.model.Post;
import com.moots.api_busca.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
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

        log.info("Post salvo no elastic com sucesso ");
        return postRepository.save(post);
    }

    public Post atualizarPostElastic(ElasticEvent elasticEvent){
        Post post = postRepository.findByPostId(elasticEvent.getPostId().toString());

        post.setTag(elasticEvent.getTag());
        post.setListImagens(elasticEvent.getListImagens());
        post.setTexto(elasticEvent.getTexto());
        post.setContadorLike(elasticEvent.getContadorLike());
        post.setContadorDeslike(elasticEvent.getContadorDeslike());
        post.setNomeCompleto(elasticEvent.getNomeCompleto());
        post.setFotoPerfil(elasticEvent.getFotoPerfil());
        post.setUserId(elasticEvent.getUserId());
        post.setPostId(elasticEvent.getPostId());

        log.info("Post alterado no elastic search com sucesso !");
        postRepository.save(post);
        return post;
    }


    public Post deletarPost(String postId){
        Post post = postRepository.findByPostId(postId);
        log.info("Post deletado no elastic search com sucesso");
        postRepository.deleteById(post.getId());
        return post;
    }

    public Iterable<Post> findAll(){
        return postRepository.findAll();
    }

    public List<Post> findByTextoOrTag(String query){
        List<Post> result = postRepository.findByTextoOrTag(query, query);
        return result;
    }

    public List<Post> findPostByUserId(String userId){
        List<Post> posts = postRepository.findByUserId(userId);
        return posts;
    }

}
