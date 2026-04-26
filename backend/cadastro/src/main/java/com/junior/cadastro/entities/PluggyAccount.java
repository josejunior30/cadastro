package com.junior.cadastro.entities;

import java.math.BigDecimal;
import java.time.Instant;
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
@Table(name = "tb_pluggy_account")
public class PluggyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pluggy_account_id", nullable = false, unique = true)
    private String pluggyAccountId;

    private String name;
    private String type;
    private String subtype;
    private String currencyCode;

    @Column(precision = 19, scale = 2)
    private BigDecimal balance;

    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private PluggyItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public PluggyAccount() {
    }

	public PluggyAccount(Long id, String pluggyAccountId, String name, String type, String subtype, String currencyCode,
			BigDecimal balance, Instant updatedAt, PluggyItem item, User user) {
		super();
		this.id = id;
		this.pluggyAccountId = pluggyAccountId;
		this.name = name;
		this.type = type;
		this.subtype = subtype;
		this.currencyCode = currencyCode;
		this.balance = balance;
		this.updatedAt = updatedAt;
		this.item = item;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPluggyAccountId() {
		return pluggyAccountId;
	}

	public void setPluggyAccountId(String pluggyAccountId) {
		this.pluggyAccountId = pluggyAccountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public PluggyItem getItem() {
		return item;
	}

	public void setItem(PluggyItem item) {
		this.item = item;
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
		PluggyAccount other = (PluggyAccount) obj;
		return Objects.equals(id, other.id);
	}

   
}