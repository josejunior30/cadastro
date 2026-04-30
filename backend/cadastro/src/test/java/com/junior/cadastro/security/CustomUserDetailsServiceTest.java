package com.junior.cadastro.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.junior.cadastro.entities.Role;
import com.junior.cadastro.entities.User;
import com.junior.cadastro.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsernameShouldReturnSecurityUserDetails() {
        User user = new User(1L, "José", "Junior", "jose@email.com", "123");
        user.getRoles().add(new Role(1L, "ROLE_USER"));

        when(userRepository.findByEmail("jose@email.com")).thenReturn(Optional.of(user));

        SecurityUserDetails result = (SecurityUserDetails) service.loadUserByUsername("jose@email.com");

        assertEquals("jose@email.com", result.getUsername());
        assertEquals(1L, result.getId());
        assertEquals(1, result.getAuthorities().size());
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUserIsMissing() {
        when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing@email.com")
        );
    }
}