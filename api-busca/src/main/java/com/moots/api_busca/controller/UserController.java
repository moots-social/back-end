package com.moots.api_busca.controller;

import com.moots.api_busca.model.User;
import com.moots.api_busca.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> findByTagOrNomeCompleto(@RequestParam String query){
        List<User> result = userService.findByTagOrNomeCompleto(query);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/curso/{curso}")
    public ResponseEntity<List<User>> findUserByCurso(@PathVariable String curso){
        List<User> result = userService.findByCurso(curso);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/curso/{curso}/query/{query}")
    public ResponseEntity<List<User>> findByCursoAndTagOrNomeCompleto(@PathVariable String curso, @PathVariable String query){
        List<User> result = userService.findByCursoAndTagOrNomeCompleto(curso, query);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<User>> findAll(){
        Iterable<User> result = userService.findAll();
        return ResponseEntity.ok().body(result);
    }
}
