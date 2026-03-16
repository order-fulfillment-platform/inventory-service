package com.ofp.inventoryservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofp.inventoryservice.dto.OrderCreatedEvent;
import com.ofp.inventoryservice.entity.OutboxEvent;
import com.ofp.inventoryservice.entity.Product;
import com.ofp.inventoryservice.entity.Reservation;
import com.ofp.inventoryservice.entity.ReservationStatus;
import com.ofp.inventoryservice.repository.OutboxEventRepository;
import com.ofp.inventoryservice.repository.ProductRepository;
import com.ofp.inventoryservice.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

	private final ProductRepository productRepository;
	private final ReservationRepository reservationRepository;
	private final OutboxEventRepository outboxEventRepository;
	private final ObjectMapper objectMapper;

	@Transactional
	public void processOrderCreated(OrderCreatedEvent event) {
		log.info("Processing ORDER_CREATED for orderId {}", event.getId());

		List<Reservation> reservations = new ArrayList<>();
		boolean allReserved = true;

		// Check availability for each item
		for(var item : event.getItems()) {
			Product product = productRepository.findById(item.getProductId())
					.orElse(null);

			if(product == null || product.getAvailableQuantity() < item.getQuantity()) {
				allReserved = false;
				log.warn("Stock unavailable for productId {}", item.getProductId());
				break;
			}
		}

		// If all available, decrease stock and create reservations
		if(allReserved) {
			for (var item : event.getItems()) {
				Product product = productRepository.findById(item.getProductId()).get();
				product.setAvailableQuantity(product.getAvailableQuantity() - item.getQuantity());
				productRepository.save(product);

				reservations.add(Reservation.builder()
						.orderId(event.getId())
						.productId(item.getProductId())
						.quantityReserved(item.getQuantity())
						.status(ReservationStatus.RESERVED)
						.build());
			}

			reservationRepository.saveAll(reservations);
			createOutboxEvent(event.getId(), "STOCK_RESERVED", event.getCustomerId(), event.getTotalAmount());
			log.info("Stock reserved for orderId {}", event.getId());

		} else {
			// Insufficient stock, emit STOCK_REJECTED
			createOutboxEvent(event.getId(), "STOCK_REJECTED", event.getCustomerId(), event.getTotalAmount());
			log.warn("Stock rejected for orderId {}", event.getId());
		}
	}

	private void createOutboxEvent(UUID orderId, String eventType, UUID customerId, BigDecimal totalAmount) {
		try {
			Map<String, String> payloadMap = new HashMap<>();
			payloadMap.put("orderId", orderId.toString());
			payloadMap.put("customerId", customerId.toString());
			payloadMap.put("totalAmount", totalAmount.toString());

			String payload = objectMapper.writeValueAsString(payloadMap);

			OutboxEvent event = OutboxEvent.builder()
					.aggregateId(orderId)
					.eventType(eventType)
					.payload(payload)
					.build();

			outboxEventRepository.save(event);
		} catch (Exception e) {
			log.error("Failed to create outbox event {}", eventType, e);
			throw new RuntimeException("Failed to create outbox event", e);
		}
	}
}
