package com.junior.cadastro.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.junior.cadastro.entities.User;
import com.junior.cadastro.exceptions.PluggyIntegrationException;
import com.junior.cadastro.repository.UserRepository;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new PluggyIntegrationException("Nenhum usuário autenticado encontrado.");
        }

        String email;

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            email = jwt.getClaimAsString("email");
        } else {
            email = authentication.getName();
        }

        if (email == null || email.isBlank()) {
            email = authentication.getName();
        }

        final String resolvedEmail = email;

        return userRepository.findByEmail(resolvedEmail)
                .orElseThrow(() -> new PluggyIntegrationException(
                        "Usuário autenticado não encontrado: " + resolvedEmail
                ));
    }

    public User getUserByClientUserId(String clientUserId) {
        Long userId;

        try {
            userId = Long.valueOf(clientUserId);
        } catch (NumberFormatException e) {
            throw new PluggyIntegrationException("clientUserId inválido: " + clientUserId, e);
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new PluggyIntegrationException(
                        "Usuário não encontrado para clientUserId=" + clientUserId
                ));
    }
}