package com.junior.cadastro.controller;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.junior.cadastro.DTO.LoginRequest;
import com.junior.cadastro.exceptions.ApiError;
import com.junior.cadastro.security.JwtService;
import com.junior.cadastro.util.HttpRequestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints de login e emissão de JWT")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(
        summary = "Realiza login",
        description = "Autentica o usuário e retorna um token JWT."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Login realizado com sucesso",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = "{\"token\":\"jwt-token\"}")
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "JSON inválido ou corpo da requisição malformado",
        content = @Content(schema = @Schema(implementation = ApiError.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = "Credenciais inválidas",
        content = @Content(schema = @Schema(implementation = ApiError.class))
    )
    @ApiResponse(
        responseCode = "422",
        description = "Erro de validação nos campos enviados",
        content = @Content(schema = @Schema(implementation = ApiError.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ApiError.class))
    )
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        final String ip = HttpRequestUtils.clientIp(http);

        try {
            log.info("Login tentativa email={} ip={}", request.getEmail(), ip);

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            String jwt = jwtService.generateToken(auth);

            log.info("Login OK email={} ip={}", request.getEmail(), ip);

            return ResponseEntity.ok(Map.of("token", jwt));

        } catch (BadCredentialsException e) {
            log.warn("Login falhou email={} ip={}", request.getEmail(), ip);
            throw new BadCredentialsException("Email ou senha inválidos");

        } catch (AuthenticationException e) {
            log.warn("Login falhou email={} ip={} tipo={}", request.getEmail(), ip, e.getClass().getSimpleName());
            throw e;
        }
    }
}