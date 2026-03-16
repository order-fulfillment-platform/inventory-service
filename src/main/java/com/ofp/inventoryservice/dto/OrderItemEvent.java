package com.ofp.inventoryservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class OrderItemEvent {
	private UUID id;
	private UUID productId;
	private Integer quantity;
	private BigDecimal unitPrice;
}
