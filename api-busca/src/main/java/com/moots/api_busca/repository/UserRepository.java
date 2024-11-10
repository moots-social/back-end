package com.moots.api_busca.repository;

import com.moots.api_busca.model.User;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends ElasticsearchRepository<User, String> {

    User findByUserId(String userId);

    List<User> findByCurso(String curso);

    List<User> findByTagOrNomeCompleto(String tag, String nomeCompleto);

    @Query("{\"bool\": {\"should\": [{\"bool\": {\"must\": [{\"match\": {\"curso\": \"?0\"}}, {\"match\": {\"tag\": \"?1\"}}]}}, {\"match\": {\"nomeCompleto\": \"?2\"}}]}}")
    List<User> findCursoAndTagOrNomeCompleto(String curso, String tag, String nomeCompleto);

}
