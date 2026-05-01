package com.junior.cadastro.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
@Schema(description = "Solicitação para sincronizar um item da Pluggy")
public class PluggySyncRequest {
	@Schema(example = "item-123")
    @NotBlank
    private String itemId;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}