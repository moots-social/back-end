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

import java.time.LocalDateTime;
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

        // Busca as informações do usuário
        User user = userService.getUserRedis(userId.toString());

        post.setUserId(user.getUserId());
        post.setTexto(postDTO.texto());
        post.setFotoPerfil(user.getFotoPerfil());
        post.setNomeCompleto(user.getNomeCompleto());
        post.setTag(user.getTag());
        post.setListImagens(postDTO.listImagens());
        post.setDataCriacao(LocalDateTime.now());
        post.setComentarioList(new ArrayList<>());
        post.setContadorLike(0);
        post.setContadorDeslike(0);

        Post postSalvo = postRepository.save(post);

        postSalvo.setPostId(postSalvo.getId());

        ElasticEvent message = new ElasticEvent(post.getUserId(), post.getId(), post.getNomeCompleto(), post.getTag(), post.getFotoPerfil(), post.getTexto(), post.getListImagens(), post.getContadorLike(), post.getContadorDeslike(), null, post.getLikeUsers(), post.getDataCriacao(), null);

        kafkaProducerService.sendMessage("post-criado-topic", message);

        return postRepository.save(postSalvo);
    }

    public Post deletarPostEComentarios(Long postId) {
        ElasticEvent message = new ElasticEvent(null, postId, null, null, null, null, null, null, null, null, null, null, null);
        kafkaProducerService.sendMessage("post-deletado-topic", message);
        log.info("Evento de deletar post foi enviado com sucesso" + postId);
        return postRepository.deletarPostEComentarios(postId);
    }

    @Cacheable(value = "post", key = "#postId")
    public Post acharPostPorId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post não encontrado"));
    }

    @CacheEvict(value = "post", key = "#postId")
    public Like darLike(Long postId, boolean like) throws Exception {
        Long idUser = Utils.buscarIdToken();
        String evento = "Curtiu";

        User user = userService.getUserRedis(idUser.toString());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post não encontrado"));

        if (!post.getLikeUsers().contains(user.getUserId()) && like) {
            post.getLikeUsers().add(user.getUserId());
        } else if (!like) {
            post.getLikeUsers().remove(user.getUserId());
        }

        int contador = like ? post.getContadorLike() + 1 : post.getContadorLike() - 1;
        post.setContadorLike(contador);

        if (like && idUser.toString().equals(post.getUserId().toString())) {
            log.info("Usuário que curtiu é o mesmo que criou o post, não enviando a notificação.");
        } else if (like) {
            kafkaProducerService.sendMessage("notification-topic", new NotificationEvent(postId, idUser, user.getTag(), evento, new Date(), post.getUserId(), user.getFotoPerfil()));
            log.info("Evento enviado com sucesso para o postId: {}", postId);
        }

        ElasticEvent message = new ElasticEvent(post.getUserId().toString(), postId, post.getNomeCompleto(), user.getTag(), post.getFotoPerfil(), post.getTexto(), post.getListImagens(), post.getContadorLike(), post.getContadorDeslike(), null, post.getLikeUsers(), null, idUser);
        kafkaProducerService.sendMessage("post-atualizado-topic", message);
        log.info("Evento de alterar post foi enviado com sucesso: {}", message);

        postRepository.save(post);

        return new Like(idUser, postId, contador, like, post.getLikeUsers());
    }


    @CacheEvict(value = "post", key = "#postId")
    public Deslike darDeslike(Long postId, boolean deslike)  {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post não encontrado"));

        var idUser = Utils.buscarIdToken();

        int contador = deslike ? post.getContadorDeslike() + 1 : post.getContadorDeslike() - 1;
        post.setContadorDeslike(contador);

        kafkaProducerService.sendMessage("post-atualizado-topic", new ElasticEvent(post.getUserId().toString(), postId, post.getNomeCompleto(), post.getTag(), post.getFotoPerfil(), post.getTexto(), post.getListImagens(), post.getContadorLike(), post.getContadorDeslike(), null, post.getLikeUsers(), null, idUser));
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

        ElasticEvent colecaoPostEvent = new ElasticEvent();
        colecaoPostEvent.setUserId(post.get().getUserId().toString());
        colecaoPostEvent.setPostId(postId);
        colecaoPostEvent.setTexto(post.get().getTexto());
        colecaoPostEvent.setListImagens(post.get().getListImagens());
        colecaoPostEvent.setFotoPerfil(post.get().getFotoPerfil());
        colecaoPostEvent.setNomeCompleto(post.get().getNomeCompleto());
        colecaoPostEvent.setTag(post.get().getTag());
        colecaoPostEvent.setUserIdLogado(userId);

        kafkaProducerService.sendMessage("post-colecao-topic", colecaoPostEvent);
    }

    public List<Post> findAll(){
        return postRepository.findAllByOrderByDataCriacaoDesc();
    }

    public List<String> findLikeUsersByPostId(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(NoSuchElementException::new);

        List<String> likeUsers = post.getLikeUsers();

        return likeUsers;
    }

}
