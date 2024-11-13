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

    @KafkaListener(topics = "post-criado-topic")
    public void salvarPostElastic(ElasticEvent elasticEvent) {
        log.info("O evento de salvar post foi recebido: " + elasticEvent);
        Post post = new Post();
        post.setTag(elasticEvent.getTag());
        post.setListImagens(elasticEvent.getListImagens());
        post.setTexto(elasticEvent.getTexto());
        post.setContadorLike(elasticEvent.getContadorLike());
        post.setContadorDeslike(elasticEvent.getContadorDeslike());
        post.setNomeCompleto(elasticEvent.getNomeCompleto());
        post.setFotoPerfil(elasticEvent.getFotoPerfil());
        post.setUserId(elasticEvent.getUserId());
        post.setPostId(elasticEvent.getPostId());
        post.setLikeUsers(elasticEvent.getLikeUsers());

        postRepository.save(post);
        log.info("Post salvo com sucesso: " + post);
    }

    @KafkaListener(topics = "post-atualizado-topic")
    public void atualizarPostElastic(ElasticEvent elasticEvent){
        log.info("O evento de alterar post foi recebido " + elasticEvent);
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
        post.setLikeUsers(elasticEvent.getLikeUsers());

        postRepository.save(post);
        log.info("Post alterado no elastic search com sucesso !" + post);
    }


    @KafkaListener(topics = "post-deletado-topic")
    public void deletarPost(ElasticEvent elasticEvent){
        log.info("O evento de deletar post foi recebido " + elasticEvent.getPostId());

        Post post = postRepository.findByPostId(elasticEvent.getPostId().toString());
        postRepository.delete(post);
        log.info("Post deletado no elastic search com sucesso" + post);
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
