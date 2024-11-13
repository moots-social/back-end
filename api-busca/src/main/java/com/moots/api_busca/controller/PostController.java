package com.moots.api_busca.controller;

import com.moots.api_busca.model.Post;
import com.moots.api_busca.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/search/post")
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

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
