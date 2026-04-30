package com.junior.cadastro.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import com.junior.cadastro.entities.Role;
import com.junior.cadastro.entities.User;


@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private Authentication authentication;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(jwtEncoder, jwtDecoder, 3600);
    }

    @Test
    void generateTokenShouldEncodeAuthentication() {
        User user = new User(1L, "José", "Junior", "jose@email.com", "123");
        user.getRoles().add(new Role(1L, "ROLE_USER"));

        SecurityUserDetails principal = new SecurityUserDetails(user);

        Jwt encodedJwt = new Jwt(
                "jwt-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                Map.of("sub", "1")
        );

        Collection<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_USER"));

        when(authentication.getPrincipal()).thenReturn(principal);
        doReturn(authorities).when(authentication).getAuthorities();
        when(jwtEncoder.encode(any())).thenReturn(encodedJwt);

        String token = jwtService.generateToken(authentication);

        assertEquals("jwt-token", token);
    }

    @Test
    void parseTokenShouldDelegateToDecoder() {
        Jwt decodedJwt = new Jwt(
                "jwt-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                Map.of("sub", "1")
        );

        when(jwtDecoder.decode("jwt-token")).thenReturn(decodedJwt);

        Jwt result = jwtService.parseToken("jwt-token");

        assertSame(decodedJwt, result);
    }
}