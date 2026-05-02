package com.junior.cadastro.service;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.junior.cadastro.entities.User;
import com.junior.cadastro.exceptions.ResourceNotFoundException;
import com.junior.cadastro.repository.UserRepository;
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Usuário não autenticado.");
        }

        String email;

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            email = jwt.getClaimAsString("email");
        } else {
            email = authentication.getName();
        }

        if (email == null || email.isBlank() || "anonymousUser".equals(email)) {
            throw new AuthenticationCredentialsNotFoundException("Usuário não autenticado.");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário autenticado não encontrado: " + email
                ));
    }

    public User getUserByClientUserId(String clientUserId) {
        Long userId;

        try {
            userId = Long.valueOf(clientUserId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("clientUserId inválido: " + clientUserId, e);
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado para clientUserId=" + clientUserId
                ));
    }
}