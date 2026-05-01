package com.junior.cadastro.entities.enuns;

import java.util.Arrays;
import java.util.Optional;

public enum PluggyWebhookEventType {

	 ITEM_CREATED("item/created"),
	    ITEM_UPDATED("item/updated"),
	    ITEM_ERROR("item/error"),
	    ITEM_DELETED("item/deleted"),
	    ITEM_LOGIN_SUCCEEDED("item/login_succeeded"),
	    ITEM_WAITING_USER_INPUT("item/waiting_user_input"),
	    TRANSACTIONS_CREATED("transactions/created"),
	    TRANSACTIONS_UPDATED("transactions/updated"),
	    TRANSACTIONS_DELETED("transactions/deleted");

	    private final String value;

	    PluggyWebhookEventType(String value) {
	        this.value = value;
	    }

	    public static Optional<PluggyWebhookEventType> fromValue(String value) {
	        return Arrays.stream(values())
	                .filter(type -> type.value.equals(value))
	                .findFirst();
	    }
	}