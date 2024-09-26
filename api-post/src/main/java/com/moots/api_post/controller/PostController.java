package com.moots.api_post.controller;

import com.moots.api_post.service.SseService;
import com.moots.api_post.service.UserEventService;
import com.moots.api_post.event.ReportPostEvent;
import com.moots.api_post.dto.PostDTO;
import com.moots.api_post.model.Post;
import com.moots.api_post.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.util.List;

@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserEventService userEventService;

    @Autowired
    private SseService sseService;

    @PostMapping("/criar")
    public ResponseEntity<Post> criarPost(@RequestBody PostDTO postDTO) throws Exception {
        var postCriado = postService.criarPost(postDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(postCriado);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> acharPostPorId(@PathVariable Long postId){
        var post = postService.acharPostPorId(postId);
        return ResponseEntity.ok().body(post);
    }

    @PostMapping("/dar-like")
    public ResponseEntity<Post> darLikePost(@RequestParam Long postId, @RequestParam boolean like) {
        try{
            Post postAtualizado = postService.darLike(postId, like);

            return ResponseEntity.ok(postAtualizado);
        } catch (Exception e) {
            log.info("Erro ao dar like ao post: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/dar-deslike")
    public Post darDeslikePost(@RequestParam Long postId, @RequestParam boolean deslike){
        return postService.darDeslike(postId, deslike);
    }

    @PostMapping("/criar-report")
    public String fazerDenuncia(@RequestBody ReportPostEvent reportPostEvent){
        return postService.fazerDenuncia(reportPostEvent);
    }

    @PostMapping("/salvar-post-colecao")
    public void salvarPostColecao(@RequestParam Long postId) {
        postService.salvarPostColecao(postId);
    }

    @DeleteMapping("/deletar/{postId}")
    public ResponseEntity<Post> deletarPostEComentarios(@PathVariable Long postId){
        var postDeletado = postService.deletarPostEComentarios(postId);
        return ResponseEntity.ok().body(postDeletado);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<Post>> findAll(){
        var posts = postService.findAll();
        return ResponseEntity.ok().body(posts);
    }
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPosts() {
        SseEmitter emitter = new SseEmitter();
        sseService.addEmitter(emitter);
        return emitter;
    }
}
