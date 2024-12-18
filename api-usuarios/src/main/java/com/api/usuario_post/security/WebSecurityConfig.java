package com.api.usuario_post.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final String[] SWAGGER_WHITELIST = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**"
    };

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.headers(frameOption -> frameOption.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(athz -> athz
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/criar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/buscarEmail").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/buscar/perfil/{id}").permitAll()
                        .requestMatchers(HttpMethod.PUT,  "/user/seguir").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH,"/user/redefinir-senha/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/user/buscar/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/user/buscar-seguidores/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/user/buscar-quem-segue/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/user/colecao-salvos/{userId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/user/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/user").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/user/images").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/index.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf(c -> c.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
        return http.build();
    }
}
