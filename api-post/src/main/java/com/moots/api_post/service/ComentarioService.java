package com.moots.api_post.service;

import com.moots.api_post.dto.ComentarioDTO;
import com.moots.api_post.event.NotificationEvent;
import com.moots.api_post.handler.BusinessException;
import com.moots.api_post.model.Comentario;
import com.moots.api_post.model.Post;
import com.moots.api_post.repository.ComentarioRepository;
import com.moots.api_post.repository.PostRepository;
import com.moots.api_post.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@Slf4j
public class ComentarioService {

    @Autowired
    private UserService userService;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Transactional
    @CacheEvict(value = "post", key = "#postId")
    public Comentario adicionarComentario(Long postId, ComentarioDTO comentarioDTO) throws Exception {
        var userId = Utils.buscarIdToken();
        String evento = "Comentou";

        var user = userService.getUserRedis(userId.toString());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado"));

        Comentario comentario = new Comentario();
        comentario.setTexto(comentarioDTO.texto());
        comentario.setPost(post);
        comentario.setPostId(post.getPostId());
        comentario.setTag(user.getTag());
        comentario.setNomeCompleto(user.getNomeCompleto());
        comentario.setFotoPerfil(user.getFotoPerfil());
        comentario.setUserId(user.getUserId());

        kafkaProducerService.sendMessage("notification-topic", new NotificationEvent(postId, userId , user.getTag(), evento, new Date(), post.getUserId()));
        log.info("Evento enviado com sucesso");

        return comentarioRepository.save(comentario);
    }

    @CacheEvict(value = "post", key = "#postId")
    public Comentario deletarComentario(Long comentarioId, Long postId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new NoSuchElementException("Comentario não encontrado"));
        comentarioRepository.deleteById(comentarioId);
        return comentario;
    }



}
