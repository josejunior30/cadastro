package com.junior.cadastro.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.junior.cadastro.entities.PluggyAccount;
import com.junior.cadastro.entities.PluggyItem;
import com.junior.cadastro.entities.User;
import com.junior.cadastro.repository.PluggyAccountRepository;

@Service
public class PluggyAccountSyncService {

    private final PluggyMapper pluggyMapper;
    private final PluggyAccountRepository accountRepository;

    public PluggyAccountSyncService(
            PluggyMapper pluggyMapper,
            PluggyAccountRepository accountRepository
    ) {
        this.pluggyMapper = pluggyMapper;
        this.accountRepository = accountRepository;
    }

    public PluggyAccount saveAccount(User user, PluggyItem item, JsonNode accountNode) {
        String pluggyAccountId = accountNode.path("id").asText(null);

        PluggyAccount account = accountRepository.findByPluggyAccountId(pluggyAccountId)
                .orElseGet(PluggyAccount::new);

        account = pluggyMapper.toAccount(accountNode, account, item, user);

        return accountRepository.save(account);
    }
}