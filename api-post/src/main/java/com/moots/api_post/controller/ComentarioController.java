package com.moots.api_post.controller;

import com.moots.api_post.dto.ComentarioDTO;
import com.moots.api_post.model.Comentario;
import com.moots.api_post.service.ComentarioService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/comentario")
@Slf4j
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @PostMapping("/criar/{postId}")
    public ResponseEntity<Comentario> criarComentario(@RequestBody ComentarioDTO comentarioDTO, @PathVariable Long postId) {
        try{
            var comentarioCriado = comentarioService.adicionarComentario(postId, comentarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(comentarioCriado);
        }catch (Exception e){
            log.error("Erro ao criar comentário: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/deletar/{comentarioId}/post/{postId}")
    public ResponseEntity<Comentario> deletarComentario(@PathVariable Long comentarioId, @PathVariable Long postId) {
        var comentarioDeletado = comentarioService.deletarComentario(comentarioId, postId);
        return ResponseEntity.ok().body(comentarioDeletado);
    }
}
