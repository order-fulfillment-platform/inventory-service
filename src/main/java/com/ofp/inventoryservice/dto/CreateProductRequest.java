package com.ofp.inventoryservice.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CreateProductRequest {
	private String name;
	private BigDecimal price;
	private Integer availableQuantity;
}
