package com.moots.api_post.security;

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
                .cors(cors -> cors.disable()) // Desabilita o CORS (para simplificar)
                .addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(athz -> athz
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.GET, "/post/{postId}").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/post/criar").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/post/salvar-post-colecao").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/post/deletar/{postId}").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/post/dar-like").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/post/dar-deslike").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/post/criar-report").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/post/user/{userId}").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/comentario/criar/{postId}").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/comentario/deletar/{comentarioId}").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/post/sse").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
                )
                .csrf(c -> c.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
        return http.build();
    }
}
