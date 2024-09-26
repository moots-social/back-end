package com.moots.api_post.utils;

import com.moots.api_post.handler.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Utils {

    public static Long buscarIdToken(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BusinessException("Usuário não autenticado.");
        }
        Long idUser = (Long) authentication.getPrincipal();
        return idUser;
    }
}
