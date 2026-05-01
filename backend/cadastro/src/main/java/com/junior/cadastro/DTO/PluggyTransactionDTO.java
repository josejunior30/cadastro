package com.junior.cadastro.DTO;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.junior.cadastro.entities.PluggyTransaction;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Transação sincronizada da Pluggy")
public class PluggyTransactionDTO {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "trx-123")
    private String pluggyTransactionId;

    @Schema(example = "2026-04-30")
    private LocalDate date;

    @Schema(example = "Supermercado")
    private String description;

    @Schema(example = "89.90")
    private BigDecimal amount;

    @Schema(example = "BRL")
    private String currencyCode;

    @Schema(example = "FOOD")
    private String category;

    @Schema(example = "POSTED")
    private String status;

    @Schema(example = "DEBIT")
    private String type;

    @Schema(example = "2026-04-30T10:15:00Z")
    private Instant importedAt;

    @Schema(example = "1")
    private Long accountId;

    @Schema(example = "Conta Corrente")
    private String accountName;

    public PluggyTransactionDTO() {
    }

    public PluggyTransactionDTO(PluggyTransaction entity) {
        this.id = entity.getId();
        this.pluggyTransactionId = entity.getPluggyTransactionId();
        this.date = entity.getDate();
        this.description = entity.getDescription();
        this.amount = entity.getAmount();
        this.currencyCode = entity.getCurrencyCode();
        this.category = entity.getCategory();
        this.status = entity.getStatus();
        this.type = entity.getType();
        this.importedAt = entity.getImportedAt();

        if (entity.getAccount() != null) {
            this.accountId = entity.getAccount().getId();
            this.accountName = entity.getAccount().getName();
        }
    }

    public Long getId() {
        return id;
    }

    public String getPluggyTransactionId() {
        return pluggyTransactionId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public Instant getImportedAt() {
        return importedAt;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }
}