package com.junior.cadastro.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.junior.cadastro.DTO.ConnectTokenResponse;
import com.junior.cadastro.DTO.PluggyAccountDTO;
import com.junior.cadastro.DTO.PluggyTransactionDTO;
import com.junior.cadastro.entities.PluggyItem;
import com.junior.cadastro.entities.User;
import com.junior.cadastro.entities.enuns.PluggySyncStatus;
import com.junior.cadastro.exceptions.PluggyIntegrationException;
import com.junior.cadastro.repository.PluggyAccountRepository;
import com.junior.cadastro.repository.PluggyItemRepository;
import com.junior.cadastro.repository.PluggyTransactionRepository;

@Service
public class PluggyService {

    private static final Logger log = LoggerFactory.getLogger(PluggyService.class);

    private final PluggyClientService pluggyClientService;
    private final CurrentUserService currentUserService;
    private final PluggySyncService pluggySyncService;
    private final PluggyAccountRepository accountRepository;
    private final PluggyItemRepository itemRepository;
    private final PluggyTransactionRepository transactionRepository;

    public PluggyService(
            PluggyClientService pluggyClientService,
            CurrentUserService currentUserService,
            PluggySyncService pluggySyncService,
            PluggyAccountRepository accountRepository,
            PluggyItemRepository itemRepository,
            PluggyTransactionRepository transactionRepository
    ) {
        this.pluggyClientService = pluggyClientService;
        this.currentUserService = currentUserService;
        this.pluggySyncService = pluggySyncService;
        this.accountRepository = accountRepository;
        this.itemRepository = itemRepository;
        this.transactionRepository = transactionRepository;
    }

    public ConnectTokenResponse createConnectToken() {
        User user = currentUserService.getAuthenticatedUser();

        String accessToken = pluggyClientService.createConnectToken(
                String.valueOf(user.getId())
        );

        return new ConnectTokenResponse(accessToken);
    }

    @Transactional
    public void syncItem(String itemId) {
        User user = currentUserService.getAuthenticatedUser();
        pluggySyncService.syncItemForUser(user, itemId);
    }

    @Transactional
    public void syncItemFromWebhook(String itemId, String clientUserId) {
        User user = currentUserService.getUserByClientUserId(clientUserId);
        pluggySyncService.syncItemForUser(user, itemId);
    }

    @Transactional(readOnly = true)
    public List<PluggyAccountDTO> findMyAccounts() {
        User user = currentUserService.getAuthenticatedUser();

        return accountRepository.findByUserOrderByNameAsc(user)
                .stream()
                .map(PluggyAccountDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<PluggyTransactionDTO> findMyTransactionsByAccount(Long accountId, Pageable pageable) {
        User user = currentUserService.getAuthenticatedUser();

        boolean accountBelongsToUser = accountRepository.findByIdAndUser(accountId, user).isPresent();

        if (!accountBelongsToUser) {
            throw new PluggyIntegrationException("Conta não encontrada para o usuário autenticado.");
        }

        return transactionRepository
                .findByUserAndAccountIdOrderByDateDesc(user, accountId, pageable)
                .map(PluggyTransactionDTO::new);
    }

    @Transactional
    public void markItemAsErrorFromWebhook(String itemId, Map<String, Object> error) {
        if (itemId == null || itemId.isBlank()) {
            log.warn("Webhook item/error recebido sem itemId.");
            return;
        }

        PluggyItem item = itemRepository.findByPluggyItemId(itemId)
                .orElseGet(() -> new PluggyItem(itemId, null));

        item.setSyncStatus(PluggySyncStatus.DELETED);
        item.setLastSyncError(formatWebhookError(error));
        item.setLastSyncAt(Instant.now());

        itemRepository.save(item);

        log.warn("Item Pluggy marcado como ERROR via webhook. itemId={} error={}", itemId, error);
    }

    @Transactional
    public void markItemAsDeletedFromWebhook(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            log.warn("Webhook item/deleted recebido sem itemId.");
            return;
        }

        itemRepository.findByPluggyItemId(itemId).ifPresentOrElse(item -> {
        	item.setSyncStatus(PluggySyncStatus.ERROR);
            item.setLastSyncAt(Instant.now());
            itemRepository.save(item);

            log.info("Item Pluggy marcado como DELETED via webhook. itemId={}", itemId);
        }, () -> {
            log.warn("Item Pluggy não encontrado para marcar como DELETED. itemId={}", itemId);
        });
    }

    private String formatWebhookError(Map<String, Object> error) {
        if (error == null || error.isEmpty()) {
            return "Erro recebido via webhook Pluggy.";
        }

        String message = String.valueOf(
                error.getOrDefault("message", "Erro recebido via webhook Pluggy.")
        );

        String code = String.valueOf(
                error.getOrDefault("code", "")
        );

        if (code.isBlank() || "null".equalsIgnoreCase(code)) {
            return message;
        }

        return code + " - " + message;
    }
    
    @Transactional
    public void syncItemFromWebhookByItemId(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new PluggyIntegrationException("Webhook Pluggy sem itemId.");
        }

        User user = itemRepository.findByPluggyItemId(itemId)
                .map(item -> item.getUser())
                .orElseThrow(() -> new PluggyIntegrationException(
                        "Item Pluggy não encontrado para sincronização por itemId=" + itemId
                ));

        if (user == null) {
            throw new PluggyIntegrationException(
                    "Item Pluggy sem usuário vinculado. itemId=" + itemId
            );
        }

        pluggySyncService.syncItemForUser(user, itemId);
    }
}