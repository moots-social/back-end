package com.moots.api_busca.controller;

import com.moots.api_busca.event.ElasticEvent;
import com.moots.api_busca.model.Post;
import com.moots.api_busca.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/search/post")
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    @KafkaListener(topics = "post-salvar-topic")
    public void salvarPostElastic(ElasticEvent elasticEvent){
        System.out.println("Mensagem recebida " + elasticEvent);
        postService.salvarPostElastic(elasticEvent);
        log.info("Post salvo no Elastic Search");
    }

    @KafkaListener(topics = "post-deletado-topic")
    public void deletarPostElastic(ElasticEvent elasticEvent){
        System.out.println("Mensagem recebida " + elasticEvent);
        postService.deletarPost(elasticEvent.getPostId().toString());
        log.info("Post deletado no Elastic Search");
    }

    @KafkaListener(topics = "post-atualizado-topic")
    public void atualizarPostElastic(ElasticEvent elasticEvent){
        System.out.println("Mensagem recebida " + elasticEvent);
        postService.atualizarPostElastic(elasticEvent);
        log.info("Post atualizado no Elastic Search");
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<Post>> allPosts(){
        Iterable<Post> posts = postService.findAll();
        return ResponseEntity.ok().body(posts);
    }

    @GetMapping
    public ResponseEntity<List<Post>> searchByTexto(@RequestParam String query){
        List<Post> result = postService.findByTextoOrTag(query);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Post>> searchByUserId(@PathVariable String userId){
        List<Post> result = postService.findPostByUserId(userId);
        return ResponseEntity.ok().body(result);
    }
}
