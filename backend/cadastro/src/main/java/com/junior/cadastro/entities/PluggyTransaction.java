package com.junior.cadastro.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_pluggy_transaction")
public class PluggyTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pluggy_transaction_id", nullable = false, unique = true)
    private String pluggyTransactionId;

    private LocalDate date;

    @Column(length = 500)
    private String description;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    private String currencyCode;
    private String category;
    private String status;
    private String type;

    private Instant importedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private PluggyAccount account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public PluggyTransaction() {
    }

	public PluggyTransaction(Long id, String pluggyTransactionId, LocalDate date, String description, BigDecimal amount,
			String currencyCode, String category, String status, String type, Instant importedAt, PluggyAccount account,
			User user) {
		super();
		this.id = id;
		this.pluggyTransactionId = pluggyTransactionId;
		this.date = date;
		this.description = description;
		this.amount = amount;
		this.currencyCode = currencyCode;
		this.category = category;
		this.status = status;
		this.type = type;
		this.importedAt = importedAt;
		this.account = account;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPluggyTransactionId() {
		return pluggyTransactionId;
	}

	public void setPluggyTransactionId(String pluggyTransactionId) {
		this.pluggyTransactionId = pluggyTransactionId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Instant getImportedAt() {
		return importedAt;
	}

	public void setImportedAt(Instant importedAt) {
		this.importedAt = importedAt;
	}

	public PluggyAccount getAccount() {
		return account;
	}

	public void setAccount(PluggyAccount account) {
		this.account = account;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PluggyTransaction other = (PluggyTransaction) obj;
		return Objects.equals(id, other.id);
	}

   
}
