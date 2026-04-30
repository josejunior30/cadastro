package com.junior.cadastro.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import com.junior.cadastro.DTO.LoginRequest;
import com.junior.cadastro.security.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(authenticationManager, jwtService);
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        LoginRequest request = mock(LoginRequest.class);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        Authentication authentication = mock(Authentication.class);

        when(request.getEmail()).thenReturn("jose@email.com");
        when(request.getPassword()).thenReturn("123456");
        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(authentication)).thenReturn("jwt-token");

        ResponseEntity<?> response = controller.login(request, httpRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("jwt-token", ((Map<?, ?>) response.getBody()).get("token"));
    }

    @Test
    void loginShouldTranslateBadCredentialsException() {
        LoginRequest request = mock(LoginRequest.class);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);

        when(request.getEmail()).thenReturn("jose@email.com");
        when(request.getPassword()).thenReturn("wrong");
        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> controller.login(request, httpRequest)
        );

        assertEquals("Email ou senha inválidos", exception.getMessage());
    }
}

