package com.junior.cadastro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.cadastro.entities.PluggyAccount;
import com.junior.cadastro.entities.PluggyItem;
import com.junior.cadastro.entities.PluggyTransaction;
import com.junior.cadastro.entities.User;
import com.junior.cadastro.exceptions.PluggyIntegrationException;


public class PluggyMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private PluggyMapper mapper;
    private User user;

    @BeforeEach
    void setUp() {
        mapper = new PluggyMapper();
        user = new User(1L, "José", "Junior", "jose@email.com", "123");
    }

    @Test
    void toAccountShouldMapFields() throws Exception {
        JsonNode accountNode = objectMapper.readTree("""
                {
                  "id": "acc-1",
                  "name": "Conta Corrente",
                  "type": "BANK",
                  "subtype": "CHECKING_ACCOUNT",
                  "currencyCode": "BRL",
                  "balance": 150.75
                }
                """);

        PluggyAccount account = new PluggyAccount();
        PluggyItem item = new PluggyItem("item-1", user);

        PluggyAccount result = mapper.toAccount(accountNode, account, item, user);

        assertEquals("acc-1", result.getPluggyAccountId());
        assertEquals("Conta Corrente", result.getName());
        assertEquals("BANK", result.getType());
        assertEquals("CHECKING_ACCOUNT", result.getSubtype());
        assertEquals("BRL", result.getCurrencyCode());
        assertEquals(new BigDecimal("150.75"), result.getBalance());
        assertNotNull(result.getUpdatedAt());
        assertSame(item, result.getItem());
        assertSame(user, result.getUser());
    }

    @Test
    void toAccountShouldThrowWhenIdIsMissing() throws Exception {
        JsonNode accountNode = objectMapper.readTree("""
                {
                  "name": "Conta sem id"
                }
                """);

        assertThrows(
                PluggyIntegrationException.class,
                () -> mapper.toAccount(accountNode, new PluggyAccount(), new PluggyItem("item-1", user), user)
        );
    }

    @Test
    void toTransactionShouldMapFields() throws Exception {
        JsonNode transactionNode = objectMapper.readTree("""
                {
                  "id": "trx-1",
                  "date": "2026-04-30T10:15:00Z",
                  "description": "Supermercado",
                  "amount": 89.90,
                  "currencyCode": "BRL",
                  "category": "FOOD",
                  "status": "POSTED",
                  "type": "DEBIT"
                }
                """);

        PluggyAccount account = new PluggyAccount();
        account.setPluggyAccountId("acc-1");

        PluggyTransaction transaction = new PluggyTransaction();

        PluggyTransaction result = mapper.toTransaction(transactionNode, transaction, account, user);

        assertEquals("trx-1", result.getPluggyTransactionId());
        assertEquals(LocalDate.of(2026, 4, 30), result.getDate());
        assertEquals("Supermercado", result.getDescription());
        assertEquals(0, new BigDecimal("89.90").compareTo(result.getAmount()));
        assertEquals("BRL", result.getCurrencyCode());
        assertEquals("FOOD", result.getCategory());
        assertEquals("POSTED", result.getStatus());
        assertEquals("DEBIT", result.getType());
        assertNotNull(result.getImportedAt());
        assertSame(account, result.getAccount());
        assertSame(user, result.getUser());
    }

    @Test
    void toTransactionShouldReturnNullDateWhenDateIsInvalid() throws Exception {
        JsonNode transactionNode = objectMapper.readTree("""
                {
                  "id": "trx-1",
                  "date": "2026-04",
                  "description": "Teste"
                }
                """);

        PluggyAccount account = new PluggyAccount();
        PluggyTransaction transaction = new PluggyTransaction();

        PluggyTransaction result = mapper.toTransaction(transactionNode, transaction, account, user);

        assertNull(result.getDate());
    }

    @Test
    void toTransactionShouldThrowWhenIdIsMissing() throws Exception {
        JsonNode transactionNode = objectMapper.readTree("""
                {
                  "description": "Sem id"
                }
                """);

        assertThrows(
                PluggyIntegrationException.class,
                () -> mapper.toTransaction(transactionNode, new PluggyTransaction(), new PluggyAccount(), user)
        );
    }
}