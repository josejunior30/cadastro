package com.junior.cadastro.entities;

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
@Table(name = "tb_pluggy_item")
public class PluggyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pluggy_item_id", nullable = false, unique = true)
    private String pluggyItemId;

    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public PluggyItem() {
    }

    public PluggyItem(String pluggyItemId, User user) {
        this.pluggyItemId = pluggyItemId;
        this.user = user;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getPluggyItemId() {
        return pluggyItemId;
    }

    public void setPluggyItemId(String pluggyItemId) {
        this.pluggyItemId = pluggyItemId;
    }

    public Instant getCreatedAt() {
        return createdAt;
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
		PluggyItem other = (PluggyItem) obj;
		return Objects.equals(id, other.id);
	}
    
    
}
