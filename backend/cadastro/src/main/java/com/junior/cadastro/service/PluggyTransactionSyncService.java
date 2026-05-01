package com.junior.cadastro.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.junior.cadastro.entities.PluggyAccount;
import com.junior.cadastro.entities.PluggyTransaction;
import com.junior.cadastro.entities.User;
import com.junior.cadastro.repository.PluggyTransactionRepository;

@Service
public class PluggyTransactionSyncService {

    private final PluggyClientService pluggyClientService;
    private final PluggyMapper pluggyMapper;
    private final PluggyTransactionRepository transactionRepository;

    public PluggyTransactionSyncService(
            PluggyClientService pluggyClientService,
            PluggyMapper pluggyMapper,
            PluggyTransactionRepository transactionRepository
    ) {
        this.pluggyClientService = pluggyClientService;
        this.pluggyMapper = pluggyMapper;
        this.transactionRepository = transactionRepository;
    }

    public int syncTransactions(User user, PluggyAccount account) {
        int page = 1;
        int totalImported = 0;

        while (true) {
            JsonNode response = pluggyClientService.fetchTransactions(
                    account.getPluggyAccountId(),
                    page
            );

            JsonNode results = response != null ? response.get("results") : null;

            if (results == null || !results.isArray() || results.size() == 0) {
                break;
            }

            for (JsonNode transactionNode : results) {
                saveTransaction(user, account, transactionNode);
                totalImported++;
            }

            if (results.size() < 500) {
                break;
            }

            page++;
        }

        return totalImported;
    }

    private void saveTransaction(User user, PluggyAccount account, JsonNode transactionNode) {
        String pluggyTransactionId = transactionNode.path("id").asText(null);

        PluggyTransaction transaction = transactionRepository
                .findByPluggyTransactionId(pluggyTransactionId)
                .orElseGet(PluggyTransaction::new);

        transaction = pluggyMapper.toTransaction(transactionNode, transaction, account, user);

        transactionRepository.save(transaction);
    }
}