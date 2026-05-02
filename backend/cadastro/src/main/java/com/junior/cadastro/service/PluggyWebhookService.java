package com.junior.cadastro.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.junior.cadastro.DTO.PluggyWebhookEvent;
import com.junior.cadastro.exceptions.PluggyIntegrationException;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class PluggyWebhookService {

	private static final Logger log = LoggerFactory.getLogger(PluggyWebhookService.class);

	private final PluggyService pluggyService;
	private final MeterRegistry meterRegistry;

	public PluggyWebhookService(PluggyService pluggyService, MeterRegistry meterRegistry) {
		this.pluggyService = pluggyService;
		this.meterRegistry = meterRegistry;
	}

	@Async("webhookExecutor")
	public void handle(PluggyWebhookEvent event) {
		String eventName = getEventName(event);
		Timer.Sample timer = Timer.start(meterRegistry);

		try {
			if (event == null) {
				incrementMetric("received", "event", "unknown");
				incrementMetric("ignored", "reason", "empty_body");

				log.warn("Webhook Pluggy recebido com body vazio.");
				return;
			}

			addWebhookContextToLogs(event);

			incrementMetric("received", "event", eventName);

			log.info("Webhook Pluggy recebido. event={} eventId={} itemId={} clientUserId={} triggeredBy={}",
					event.event(), event.eventId(), event.itemId(), event.clientUserId(), event.triggeredBy());

			if (!StringUtils.hasText(event.event())) {
				incrementMetric("ignored", "reason", "missing_event");

				log.warn("Webhook Pluggy sem campo event. eventId={}", event.eventId());
				return;
			}

			switch (event.event()) {
			case "item/created", "item/updated" -> {
				pluggyService.syncItemFromWebhook(event.itemId(), event.clientUserId());
				incrementMetric("processed", "event", eventName);
			}

			case "transactions/created", "transactions/updated" -> {
				pluggyService.syncItemFromWebhookByItemId(event.itemId());
				incrementMetric("processed", "event", eventName);
			}

			case "item/error" -> {
				pluggyService.markItemAsErrorFromWebhook(event.itemId(), event.error());
				incrementMetric("processed", "event", eventName);
			}

			case "item/deleted" -> {
				pluggyService.markItemAsDeletedFromWebhook(event.itemId());
				incrementMetric("processed", "event", eventName);
			}

			case "item/waiting_user_input", "item/login_succeeded" -> {
				incrementMetric("informational", "event", eventName);

				log.info("Evento Pluggy informativo recebido. event={} itemId={}", event.event(), event.itemId());
			}

			default -> {
				incrementMetric("ignored", "reason", "unsupported_event");

				log.info("Evento Pluggy ignorado. event={}", event.event());
			}
			}

		} catch (PluggyIntegrationException e) {
			incrementMetric("error", "event", eventName, "exception", e.getClass().getSimpleName());

			log.error("Erro de integração ao processar webhook Pluggy. event={} message={}", eventName, e.getMessage(),
					e);

		} catch (Exception e) {
			incrementMetric("error", "event", eventName, "exception", e.getClass().getSimpleName());

			log.error("Erro inesperado ao processar webhook Pluggy. event={}", eventName, e);

		} finally {
			timer.stop(Timer.builder("pluggy.webhook.duration").tag("event", eventName).register(meterRegistry));

			MDC.clear();
		}
	}

	private String getEventName(PluggyWebhookEvent event) {
		if (event == null || !StringUtils.hasText(event.event())) {
			return "unknown";
		}

		return event.event();
	}

	private void addWebhookContextToLogs(PluggyWebhookEvent event) {
		Map<String, String> logContext = new LinkedHashMap<>();

		logContext.put("pluggyEvent", event.event());
		logContext.put("pluggyEventId", event.eventId());
		logContext.put("pluggyItemId", event.itemId());
		logContext.put("pluggyClientUserId", event.clientUserId());
		logContext.put("pluggyTriggeredBy", event.triggeredBy());

		logContext.entrySet().stream().filter(entry -> StringUtils.hasText(entry.getValue()))
				.forEach(entry -> MDC.put(entry.getKey(), entry.getValue()));
	}

	private void incrementMetric(String metricName, String... tags) {
		meterRegistry.counter("pluggy.webhook." + metricName, tags).increment();
	}
}
