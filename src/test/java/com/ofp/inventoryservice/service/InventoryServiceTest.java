package com.ofp.inventoryservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofp.inventoryservice.dto.OrderCreatedEvent;
import com.ofp.inventoryservice.dto.OrderItemEvent;
import com.ofp.inventoryservice.entity.Product;
import com.ofp.inventoryservice.repository.OutboxEventRepository;
import com.ofp.inventoryservice.repository.ProductRepository;
import com.ofp.inventoryservice.repository.ReservationRepository;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private OutboxEventRepository outboxEventRepository;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private InventoryService inventoryService;


	@Test
	void processOrderCreated_shouldReserveStock_whenProductsAvailable() throws Exception {
		// Arrange
		UUID productId = UUID.fromString("b7e9f3a2-1234-5678-abcd-ef0123456789");

		OrderItemEvent item = new OrderItemEvent();
		item.setProductId(productId);
		item.setUnitPrice(new BigDecimal("29.99"));
		item.setQuantity(2);

		OrderCreatedEvent event = new OrderCreatedEvent();
		event.setId(UUID.randomUUID());
		event.setCustomerId(UUID.randomUUID());
		event.setItems(List.of(item));

		Product product = Product.builder()
				.id(productId)
				.name("Product A")
				.availableQuantity(100)
				.price(new BigDecimal("29.00"))
				.build();

		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		when(reservationRepository.saveAll(any())).thenReturn(List.of());
		when(objectMapper.writeValueAsString(any())).thenReturn("{}");
		when(outboxEventRepository.save(any())).thenReturn(null);

		// Act
		inventoryService.processOrderCreated(event);

		// Assert
		verify(reservationRepository, times(1)).saveAll(any());
		verify(outboxEventRepository, times(1)).save(any());
		verify(productRepository, times(2)).findById(productId);
	}

	@Test
	void processOrderCreated_shouldRejectStock_whenProductNotFound() throws Exception {
		// Arrange
		UUID productId = UUID.randomUUID();

		OrderItemEvent item = new OrderItemEvent();
		item.setProductId(productId);
		item.setUnitPrice(new BigDecimal("29.99"));
		item.setQuantity(2);

		OrderCreatedEvent event = new OrderCreatedEvent();
		event.setId(UUID.randomUUID());
		event.setCustomerId(UUID.randomUUID());
		event.setItems(List.of(item));

		when(productRepository.findById(productId)).thenReturn(Optional.empty());
		when(objectMapper.writeValueAsString(any())).thenReturn("{}");
		when(outboxEventRepository.save(any())).thenReturn(null);

		// Act
		inventoryService.processOrderCreated(event);

		// Assert
		verify(reservationRepository, never()).saveAll(any());
		verify(outboxEventRepository, times(1)).save(any());
	}


	@Test
	void processOrderCreated_shouldRejectStock_whenQuantityInsufficent() throws Exception {
		// Arrange
		UUID productId = UUID.randomUUID();

		OrderItemEvent item = new OrderItemEvent();
		item.setProductId(productId);
		item.setUnitPrice(new BigDecimal("29.99"));
		item.setQuantity(10);

		OrderCreatedEvent event = new OrderCreatedEvent();
		event.setId(UUID.randomUUID());
		event.setCustomerId(UUID.randomUUID());
		event.setItems(List.of(item));

		Product product = Product.builder()
					.id(productId)
					.name("Product A")
					.price(new BigDecimal("29.99"))
					.availableQuantity(5) // less than requested quantity
					.build();

		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		when(objectMapper.writeValueAsString(any())).thenReturn("{}");
		when(outboxEventRepository.save(any())).thenReturn(null);

		// Act
		inventoryService.processOrderCreated(event);

		// Assert
		verify(reservationRepository, never()).saveAll(any());
		verify(outboxEventRepository, times(1)).save(any());

	}

}
