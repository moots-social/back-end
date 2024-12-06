package com.moots.api_post.service;

import com.moots.api_post.dto.ComentarioDTO;
import com.moots.api_post.event.ElasticEvent;
import com.moots.api_post.event.NotificationEvent;
import com.moots.api_post.model.Comentario;
import com.moots.api_post.model.Post;
import com.moots.api_post.model.User;
import com.moots.api_post.repository.ComentarioRepository;
import com.moots.api_post.repository.PostRepository;
import com.moots.api_post.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;


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
    //@CacheEvict(value = "post", key = "#postId")
    public Comentario adicionarComentario(Long postId, ComentarioDTO comentarioDTO) throws Exception {
        Long userId = Utils.buscarIdToken();
        String evento = "Comentou";

        User user = userService.getUserRedis(userId.toString());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado"));

        Comentario comentario = new Comentario();
        comentario.setTexto(comentarioDTO.texto());
        comentario.setPost(post);
        comentario.setPostId(post.getId());
        comentario.setTag(user.getTag());
        comentario.setNomeCompleto(user.getNomeCompleto());
        comentario.setFotoPerfil(user.getFotoPerfil());
        comentario.setUserId(user.getUserId());

        if(userId.toString().equals(comentario.getUserId().toString())){
            log.info("Usuário que curtiu é o mesmo que criou o comentario, não enviando a notificação.");
        }else{
            kafkaProducerService.sendMessage("notification-topic", new NotificationEvent(postId, userId , user.getTag(), evento, new Date(), comentario.getUserId(), user.getFotoPerfil()));
            log.info("Evento enviado com sucesso");
        }

        return comentarioRepository.save(comentario);
    }

    //@CacheEvict(value = "post", key = "#postId")
    public Comentario deletarComentario(Long comentarioId, Long postId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new NoSuchElementException("Comentario não encontrado"));
        comentarioRepository.deleteById(comentarioId);
        return comentario;
    }

    //@CacheEvict(value = "post", key = "#elasticEvent.postId")
    public void alterarComentarioByUser(Comentario comentario, ElasticEvent elasticEvent){
        comentario.setTag(elasticEvent.getTag());
        comentario.setNomeCompleto(elasticEvent.getNomeCompleto());
        comentario.setFotoPerfil(elasticEvent.getFotoPerfil());
    }


    @KafkaListener(topics = "user-deletado-topic", groupId = "grupo-3")
    public void deletarComentarioByUserId(ElasticEvent elasticEvent){

        List<Comentario> comentarios = this.comentarioRepository.findByUserId(elasticEvent.getUserId());

        comentarios.forEach((comentario -> comentarioRepository.deleteByUserId(comentario.getUserId())));
    }


    //@CacheEvict(value = "post", key = "#elasticEvent.postId")
    @KafkaListener(topics = "user-alterado-topic", groupId = "grupo-20")
    public void alterarComentarioByUser(ElasticEvent elasticEvent){
        List<Comentario> comentarios = comentarioRepository.findByUserId(elasticEvent.getUserId());

        comentarios.forEach((comentario) -> {
            this.alterarComentarioByUser(comentario, elasticEvent);
            comentarioRepository.save(comentario);
        });
    }
}
