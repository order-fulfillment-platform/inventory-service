package com.ofp.inventoryservice.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofp.inventoryservice.dto.OrderCreatedEvent;
import com.ofp.inventoryservice.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {

	private final InventoryService inventoryService;
	private final ObjectMapper objectMapper;

	@KafkaListener(topics = "order.created", groupId = "inventory-service")
	public void consume(String message) {
		log.info("Received ORDER_CREATED event: {}", message);
		try {
			OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
			inventoryService.processOrderCreated(event);
		} catch(Exception e) {
			log.error("Failed to process ORDER_CREATED event: {}", message, e);
		}

	}

}
