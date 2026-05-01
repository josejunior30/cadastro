package com.junior.cadastro.DTO;

import java.math.BigDecimal;
import java.time.Instant;

import com.junior.cadastro.entities.PluggyAccount;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Conta sincronizada da Pluggy")
public class PluggyAccountDTO {
	@Schema(example = "1")
	private Long id;
	@Schema(example = "84241445-49d8-42ab-a3fb-9f5473b44ce1")
	private String pluggyAccountId;
	@Schema(example = "Conta Corrente")
	private String name;
	@Schema(example = "BANK")
	private String type;
	@Schema(example = "CHECKING_ACCOUNT")
	private String subtype;
	@Schema(example = "BRL")
	private String currencyCode;
	@Schema(example = "1500.75")
	private BigDecimal balance;
	@Schema(example = "2026-04-27T19:12:59.693344Z")
	private Instant updatedAt;

	public PluggyAccountDTO() {
	}

	public PluggyAccountDTO(PluggyAccount entity) {
		this.id = entity.getId();
		this.pluggyAccountId = entity.getPluggyAccountId();
		this.name = entity.getName();
		this.type = entity.getType();
		this.subtype = entity.getSubtype();
		this.currencyCode = entity.getCurrencyCode();
		this.balance = entity.getBalance();
		this.updatedAt = entity.getUpdatedAt();
	}

	public Long getId() {
		return id;
	}

	public String getPluggyAccountId() {
		return pluggyAccountId;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
