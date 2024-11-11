package com.moots.api_post.service;

import com.moots.api_post.dto.PostDTO;
import com.moots.api_post.event.NotificationEvent;
import com.moots.api_post.event.ElasticEvent;
import com.moots.api_post.event.ReportPostEvent;
import com.moots.api_post.model.Post;
import com.moots.api_post.model.User;
import com.moots.api_post.repository.PostRepository;
import com.moots.api_post.utils.Deslike;
import com.moots.api_post.utils.Like;
import com.moots.api_post.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class PostService {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    public Post criarPost(PostDTO postDTO) throws Exception {
        Long userId = Utils.buscarIdToken();
        Post post = new Post();
        User user = userService.getUserRedis(userId.toString());

        post.setUserId(user.getUserId());
        post.setTag(user.getTag());
        post.setFotoPerfil(user.getFotoPerfil());
        post.setNomeCompleto(user.getNomeCompleto());
        post.setListImagens(postDTO.listImagens());
        post.setTexto(postDTO.texto());
        post.setComentarioList(new ArrayList<>());
        post.setContadorLike(0);
        post.setContadorDeslike(0);

        Post postSalvo = postRepository.save(post);
        postSalvo.setPostId(postSalvo.getId());

        kafkaProducerService.sendMessage("post-salvo-topic", new ElasticEvent(post.getUserId(), post.getPostId(), post.getNomeCompleto(), post.getTag(), post.getFotoPerfil(), post.getTexto(), post.getListImagens(), post.getContadorLike(), post.getContadorDeslike(), null));
        log.info("Evento enviado com sucesso");

        return postSalvo;
    }


    public Post deletarPostEComentarios(Long postId) {
        kafkaProducerService.sendMessage("post-deletado-topic", new ElasticEvent(null, postId, null, null, null, null, null, null, null, null));
        return postRepository.deletarPostEComentarios(postId);
    }

    @Cacheable(value = "post", key = "#postId")
    public Post acharPostPorId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post não encontrado"));
    }

    @CacheEvict(value = "post", key = "#postId")
    public Like darLike(Long postId, boolean like) throws Exception {
        var idUser = Utils.buscarIdToken();
        String evento = "Curtiu";

        var user = userService.getUserRedis(idUser.toString());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post não encontrado"));

        int contador = like ? post.getContadorLike() + 1 : post.getContadorLike() - 1;
        post.setContadorLike(contador);


        if (like && !idUser.equals(post.getUserId())) {
            kafkaProducerService.sendMessage("notification-topic", new NotificationEvent(postId, idUser, user.getTag(), evento, new Date(), post.getUserId(), user.getFotoPerfil()));
            log.info("Evento enviado com sucesso");
        }
        kafkaProducerService.sendMessage("post-atualizado-topic", new ElasticEvent(post.getUserId().toString(), postId, post.getNomeCompleto(), user.getTag(), post.getFotoPerfil(), post.getTexto(), post.getListImagens(), post.getContadorLike(), post.getContadorDeslike(), null));
        postRepository.save(post);
        return new Like(idUser, postId, contador, like);
    }

    @CacheEvict(value = "post", key = "#postId")
    public Deslike darDeslike(Long postId, boolean deslike)  {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post não encontrado"));

        var idUser = Utils.buscarIdToken();

        int contador = deslike ? post.getContadorDeslike() + 1 : post.getContadorDeslike() - 1;
        post.setContadorDeslike(contador);

        kafkaProducerService.sendMessage("post-atualizado-topic", new ElasticEvent(post.getUserId().toString(), postId, post.getNomeCompleto(), post.getTag(), post.getFotoPerfil(), post.getTexto(), post.getListImagens(), post.getContadorLike(), post.getContadorDeslike(), null));
        postRepository.save(post);
        return new Deslike(idUser, postId, contador, deslike);
    }

    public String fazerDenuncia(ReportPostEvent reportPostEvent) {
        kafkaProducerService.sendMessage("report-post-topic", reportPostEvent);
        return "Reporte realizado com sucesso";
    }

    public void salvarPostColecao(Long postId){
        log.info("Post está sendo salvo em sua coleção");
        Long userId = Utils.buscarIdToken();

        var post = postRepository.findById(postId);

        var colecaoPostEvent = new ElasticEvent();
        colecaoPostEvent.setUserId(userId.toString());
        colecaoPostEvent.setPostId(postId);
        colecaoPostEvent.setTexto(post.get().getTexto());
        colecaoPostEvent.setListImagens(post.get().getListImagens());
        colecaoPostEvent.setContadorDeslike(post.get().getContadorDeslike());
        colecaoPostEvent.setContadorLike(post.get().getContadorLike());

        kafkaProducerService.sendMessage("post-colecao-topic", colecaoPostEvent);
    }

    public List<Post> findAll(){
        return postRepository.findAll();
    }

}
