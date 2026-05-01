package com.junior.cadastro.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.junior.cadastro.DTO.ConnectTokenResponse;
import com.junior.cadastro.DTO.PluggyAccountDTO;
import com.junior.cadastro.DTO.PluggySyncRequest;
import com.junior.cadastro.DTO.PluggyTransactionDTO;
import com.junior.cadastro.service.PluggyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/pluggy")
@Tag(name = "Pluggy", description = "Integração com Pluggy para conexão bancária e sincronização")
@SecurityRequirement(name = "bearerAuth")
public class PluggyController {

    private final PluggyService pluggyService;

    public PluggyController(PluggyService pluggyService) {
        this.pluggyService = pluggyService;
    }

    @PostMapping("/connect-token")
    @Operation(summary = "Gera connect token", description = "Gera o token necessário para abrir o Pluggy Connect.")
    @ApiResponse(
        responseCode = "200",
        description = "Token gerado com sucesso",
        content = @Content(schema = @Schema(implementation = ConnectTokenResponse.class))
    )
    public ResponseEntity<ConnectTokenResponse> createConnectToken() {
        return ResponseEntity.ok(pluggyService.createConnectToken());
    }

    @PostMapping("/items/sync")
    @Operation(summary = "Sincroniza item Pluggy", description = "Importa contas e transações do item informado.")
    @ApiResponse(responseCode = "204", description = "Item sincronizado com sucesso", content = @Content)
    public ResponseEntity<Void> syncItem(@Valid @RequestBody PluggySyncRequest request) {
        pluggyService.syncItem(request.getItemId());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/accounts")
    @Operation(summary = "Lista contas do usuário autenticado")
    @ApiResponse(
        responseCode = "200",
        description = "Contas retornadas com sucesso",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = PluggyAccountDTO.class)))
    )
    public ResponseEntity<List<PluggyAccountDTO>> findMyAccounts() {
        List<PluggyAccountDTO> accounts = pluggyService.findMyAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    @Operation(summary = "Lista transações da conta", description = "Retorna as transações paginadas da conta informada.")
    @ApiResponse(
        responseCode = "200",
        description = "Transações retornadas com sucesso",
        content = @Content(schema = @Schema(implementation = PluggyTransactionDTO.class))
    )
    public ResponseEntity<Page<PluggyTransactionDTO>> findMyTransactionsByAccount(
        @Parameter(description = "ID interno da conta", example = "1")
        @PathVariable Long accountId,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<PluggyTransactionDTO> transactions = pluggyService.findMyTransactionsByAccount(accountId, pageable);
        return ResponseEntity.ok(transactions);
    }
}
