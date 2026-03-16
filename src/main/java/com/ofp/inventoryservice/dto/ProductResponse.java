package com.ofp.inventoryservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
	private UUID id;
	private String name;
	private BigDecimal price;
	private Integer availableQuantity;
}
