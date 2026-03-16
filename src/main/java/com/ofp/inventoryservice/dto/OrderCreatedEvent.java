package com.ofp.inventoryservice.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderCreatedEvent {
	private UUID id;
	private UUID customerId;
	private String status;
	private BigDecimal totalAmount;
	private List<OrderItemEvent> items;
}
