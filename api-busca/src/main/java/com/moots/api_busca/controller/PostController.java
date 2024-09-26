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

    @KafkaListener(topics = "post-salvo-topic")
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

    @GetMapping("/all")
    public ResponseEntity<Iterable<Post>> allPosts(){
        var posts = postService.findAll();
        return ResponseEntity.ok().body(posts);
    }

    @GetMapping
    public ResponseEntity<List<Post>> searchByTexto(@RequestParam String query, @RequestParam (defaultValue = "0", value = "page") int page){
        var result = postService.findByTextoOrTag(query, page);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Post>> searchByUserId(@PathVariable String userId){
        var result = postService.findPostByUserId(userId);
        return ResponseEntity.ok().body(result);
    }
}
