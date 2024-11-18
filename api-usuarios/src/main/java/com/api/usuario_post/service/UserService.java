package com.api.usuario_post.service;

import com.api.usuario_post.dto.ResetPasswordDTO;
import com.api.usuario_post.dto.UserDTO;
import com.api.usuario_post.dto.UsuarioDiferenteDTO;
import com.api.usuario_post.event.PostEvent;
import com.api.usuario_post.event.ElasticEvent;
import com.api.usuario_post.event.NotificationEvent;
import com.api.usuario_post.event.UserEvent;
import com.api.usuario_post.handler.BusinessException;
import com.api.usuario_post.model.Post;
import com.api.usuario_post.model.User;
import com.api.usuario_post.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private KafkaProducerService kafkaProducerService;


    public User criarUsuario(@Valid UserDTO userDTO){
        // Verificar unicidade de email e tag
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new BusinessException("Email já está em uso.");
        }
        if (userRepository.findByTag(userDTO.getTag()) != null) {
            throw new BusinessException("Tag já está em uso.");
        }

        // Validar campos obrigatórios
        if (userDTO.getNomeCompleto() == null || userDTO.getNomeCompleto().trim().isEmpty()) {
            throw new BusinessException("Nome completo é obrigatório.");
        }
        if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
            throw new BusinessException("Email é obrigatório.");
        }
        if (userDTO.getSenha() == null || userDTO.getSenha().trim().isEmpty()) {
            throw new BusinessException("Senha é obrigatória.");
        }
        if (userDTO.getTag() == null || userDTO.getTag().trim().isEmpty()) {
            throw new BusinessException("Tag é obrigatória.");
        }
        if (userDTO.getRoles() == null || userDTO.getRoles().isEmpty()) {
            throw new BusinessException("Roles é obrigatório.");
        }

        User user = new User();
        user.setNomeCompleto(userDTO.getNomeCompleto());
        user.setEmail(userDTO.getEmail());

        String pass = userDTO.getSenha();
        user.setSenha(encoder.encode(pass));

        user.setTag(userDTO.getTag());
        user.setCurso(userDTO.getCurso());
        user.setDescricao(userDTO.getDescricao());

        if(userDTO.getFotoPerfil() == null){
            String fotoPadrao = "https://storageimagesmoots.blob.core.windows.net/artifact-image-container/68a77764-1c2e-4bc4-8d6b-c280ac593970.png";
            user.setFotoPerfil(fotoPadrao);
        }else{
            user.setFotoPerfil(userDTO.getFotoPerfil());
        }

//        user.setFotoCapa(userDTO.getFotoCapa());
        user.setRoles(userDTO.getRoles());

        User savedUser = userRepository.save(user);
        savedUser.setUserId(savedUser.getId());

        ElasticEvent message = new ElasticEvent(savedUser.getUserId().toString(), null, savedUser.getNomeCompleto(), savedUser.getTag(), savedUser.getFotoPerfil(), null, null, null, null, savedUser.getCurso().toString());

        kafkaProducerService.sendMessage("user-criado-topic", message);
        log.info("O evento de salvar usuario foi enviado" + message);
        return userRepository.save(savedUser);
    }

    @CacheEvict(cacheNames = "user", key = "#id")
    @Transactional
    public User alterarUsuario(Long id, UserDTO userDTO) throws RuntimeException {
        Optional<User> user = userRepository.findByUserId(id);

        if (user.isPresent()) {

            if (userDTO.getNomeCompleto() != null) {
                user.get().setNomeCompleto(userDTO.getNomeCompleto());
            }

            if (userDTO.getTag() != null){
                user.get().setTag(userDTO.getTag());
            }
            if (userDTO.getCurso() != null) {
                user.get().setCurso(userDTO.getCurso());
            }
            if (userDTO.getDescricao() != null) {
                user.get().setDescricao(userDTO.getDescricao());
            }
            if (userDTO.getFotoPerfil() != null) {
                user.get().setFotoPerfil(userDTO.getFotoPerfil());
            }
            if (userDTO.getFotoCapa() != null) {
                user.get().setFotoCapa(userDTO.getFotoCapa());
            }

            userRepository.save(user.get());
            ElasticEvent message = new ElasticEvent(user.get().getUserId().toString(), null, user.get().getNomeCompleto(), user.get().getTag(), user.get().getFotoPerfil(), null, null, null, null, user.get().getCurso().toString());
            kafkaProducerService.sendMessage("user-alterado-topic", message);
            log.info("O evento de alterar usuario foi enviado" + message);
            return userRepository.findOnlyUser(user.get().getUserId());
        } else {
            // Lançar uma exceção apropriada se o usuário não for encontrado
            throw new BusinessException("Usuário com ID " + id + " não encontrado.");
        }
    }

    public User redefinirSenha(Long id, @Valid ResetPasswordDTO resetPasswordDTO) {
        Optional<User> userOpt = userRepository.findByUserId(id);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Verifique se a senha antiga e a nova foram fornecidas
            if (resetPasswordDTO.getSenhaAntiga() == null || resetPasswordDTO.getSenhaNova() == null) {
                throw new BusinessException("Senha antiga e nova senha são obrigatórias.");
            }

            // Verifique a senha antiga
            if (!encoder.matches(resetPasswordDTO.getSenhaAntiga(), user.getSenha())) {
                throw new BusinessException("Senha antiga está incorreta.");
            }

            // Atualize a senha com a nova senha criptografada
            user.setSenha(encoder.encode(resetPasswordDTO.getSenhaNova()));
            userRepository.save(user);

            // Retorne o usuário atualizado
            return userRepository.findOnlyUser(user.getUserId());
        } else {
            throw new BusinessException("Usuário com ID " + id + " não encontrado.");
        }
    }

    @CacheEvict(value = {"seguidores", "seguindos"}, key = "#userId")
    public User seguirUsuario(Long userId, Long userId2, boolean follow) throws RuntimeException{
        Optional<User> user1 = userRepository.findByUserId(userId);
        Optional<User> user2 = userRepository.findByUserId(userId2);
        String evento = "Seguiu";

        if(follow == false){
            removeFollower(userId, userId2);
        }

        if (user1.isPresent() && user2.isPresent() && follow == true) {
            user1.get().follow(user2.get());

            userRepository.save(user1.get());

            kafkaProducerService.sendMessage("notification-topic", new NotificationEvent(null,user1.get().getUserId(), user1.get().getTag(), evento, new Date(),user2.get().getUserId().toString(), user1.get().getFotoPerfil()));
            log.info("Evento enviado com sucesso");
            return userRepository.findOnlyUser(user1.get().getUserId());
        }

        User user = user1.get();

        return user;
    }

    @Cacheable(cacheNames = "user", key = "#id")
    public User buscarUsuarioPorId(Long id) throws RuntimeException {
        User user = userRepository.findOnlyUser(id);
        if (user != null) {
            return user;
        } else {
            throw new BusinessException("User não encontrado");
        }

    }

    public UsuarioDiferenteDTO buscarUsuarioPorIdSemToken(Long id) throws RuntimeException{
        User user = userRepository.findOnlyUser(id);

        UsuarioDiferenteDTO usuarioDiferenteDTO = new UsuarioDiferenteDTO();

        usuarioDiferenteDTO.setId(user.getId());
        usuarioDiferenteDTO.setUserId(user.getUserId());
        usuarioDiferenteDTO.setFollowers(user.getFollowers());
        usuarioDiferenteDTO.setDescricao(user.getDescricao());
        usuarioDiferenteDTO.setCurso(user.getCurso().toString());
        usuarioDiferenteDTO.setFotoCapa(user.getFotoCapa());
        usuarioDiferenteDTO.setNomeCompleto(user.getNomeCompleto());
        usuarioDiferenteDTO.setTag(user.getTag());
        usuarioDiferenteDTO.setFotoPerfil(user.getFotoPerfil());

        return usuarioDiferenteDTO;
    }

    public User buscarUsuarioEmail(String email) throws RuntimeException {
        User user = userRepository.findByOnlyEmail(email);

        if(user != null){
            return user;
        } else {
            throw new BusinessException("Email e senha válidos");
        }
    }

    @Cacheable(cacheNames = "seguidores", key = "#userId")
    public List<User> buscarSeguidoresDoUsuario(Long userId) throws RuntimeException {
        List<User> seguidores = userRepository.findFollowersByUserId(userId);

        if (!seguidores.isEmpty()) {
            return seguidores;
        } else {
            throw new BusinessException("Seguidor com id" + userId + "não encontrado");
        }
    }

    @Cacheable(cacheNames = "seguindos", key = "#userId")
    public List<User> buscarQuemOUsuarioSegue(Long userId) throws RuntimeException {
        List<User> seguidores = userRepository.findFollowingByUserId(userId);

        if (!seguidores.isEmpty()) {
            return seguidores;
        } else {
            throw new BusinessException("User " + userId + "que você segue não encontrado");
        }
    }

    @CacheEvict(cacheNames = "user", key = "#id")
    public User deletarUsuarioPorId(Long id) throws RuntimeException{
        Optional<User> user = userRepository.findByUserId(id);
        if (user.isPresent()) {
            User user1 = userRepository.findOnlyUser(user.get().getUserId());
            userRepository.deleteById(id);
            ElasticEvent message = new ElasticEvent(user1.getUserId().toString(), null, null, null, null, null, null, null, null, null);
            kafkaProducerService.sendMessage("user-deletado-topic", message);
            log.info("O evento de deletar o usuário foi enviado" + message);
            return user1;
        } else {
            throw new BusinessException("Erro ao excluir usuario");
        }
    }

    public String enviarEvento(String topico, UserEvent userEvent){
        kafkaProducerService.sendMessage(topico, userEvent);
        return "Evento enviado com sucesso";
    }


    @CacheEvict(value = "colecaoPost", key = "#elasticEvent.userId")
    @KafkaListener(topics = "post-colecao-topic")
    public void salvarPostColecao(ElasticEvent elasticEvent, boolean favorito) {
        Optional<User> optionalUser = userRepository.findByUserId(Long.valueOf(elasticEvent.getUserId()));

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado com o ID: " + elasticEvent.getUserId());
        }

        User user = optionalUser.get();

        var colecao = user.getColecaoSalvos();

        var postSalvo = new PostEvent(null, Long.valueOf(elasticEvent.getUserId()), elasticEvent.getPostId(), elasticEvent.getTexto(), elasticEvent.getListImagens(), elasticEvent.getNomeCompleto(), elasticEvent.getTag(), elasticEvent.getFotoPerfil());

        if(favorito == true){
            boolean removerPost = colecao.removeIf(c -> c.getPostId().equals(elasticEvent.getPostId()));
            throw new RuntimeException("Esse post já está salvo em sua coleção" + elasticEvent.getPostId());
        }else{
            colecao.add(postSalvo);
            log.info("Colecao salvo com sucesso");
            userRepository.save(user);
        }
    }

    @CacheEvict(value = "colecaoPost", key = "#userId")
    public User removerPostColecao(Long userId, Long postId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado com o ID: " + userId);
        }

        User user = optionalUser.get();
        var colecao = user.getColecaoSalvos();
        boolean removerPost = colecao.removeIf(c -> c.getPostId().equals(postId));

//        userRepository.deletePostEventByPostId(postId);

        log.info("Post removido da coleção com sucesso");
        return userRepository.save(user);
    }

    @Cacheable(cacheNames = "colecaoPost", key = "#userId")
    public List<PostEvent> getColecaoSalvosByUserId(Long userId) {
        Optional<User> user = userRepository.findByUserId(userId);

        if (user.isPresent()) {
            return user.get().getColecaoSalvos();
        } else {
            throw new NoSuchElementException("Usuário não encontrado para o ID: " + userId);
        }
    }

    public User removeFollower(Long userId1, Long userId2){
        Optional<User> user1 = userRepository.findByUserId(userId1);

        User user = user1.get();
        var listFollowers = user.getFollowers();

        boolean removeFollower = listFollowers.removeIf(l -> l.getUserId().equals(userId2));
        log.info("Usuário removido da lista de seguidores com sucesso");
        return userRepository.save(user);
    }

    @KafkaListener(topics = "post-topic")
    public void adicionarPostNaLista(ElasticEvent elasticEvent){

        User user = userRepository.findByUserId(Long.valueOf(elasticEvent.getUserId()))
                .orElseThrow(NoSuchElementException::new);

        Post novoPost = new Post();
        novoPost.setUserId(user.getUserId().toString());
        novoPost.setPostId(elasticEvent.getPostId());
        novoPost.setTexto(elasticEvent.getTexto());
        novoPost.setFotoPerfil(elasticEvent.getFotoPerfil());
        novoPost.setNomeCompleto(elasticEvent.getNomeCompleto());
        novoPost.setTag(elasticEvent.getTag());
        novoPost.setListImagens(elasticEvent.getListImagens());

        user.getListPosts().add(novoPost);

        userRepository.save(user);
        log.info("Post adicionado na lista de usuarios com sucesso" + user);
    }

    public List<Post> findPostAndCommentByFollowers(Long userId){
        List<Post> posts = userRepository.findPostAndComentarioByFollowers(userId);
        log.info("Post adicionado na lista de usuarios com sucesso");
        return posts;
    }

    public List<Post> findPostByUserId(Long userId){
        List<Post> posts = userRepository.findPostsByUserId(userId);
        return posts;
    }

    public List<Long> findPostIdByColecoes(Long userId){
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));

        List<PostEvent> colecao = user.getColecaoSalvos();

        List<Long> listPostId = colecao.stream()
                .map(p -> p.getPostId())
                .collect(Collectors.toList());

        return listPostId;
    }
}
