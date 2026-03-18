package com.ofp.inventoryservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProductRequest {

	@NotBlank(message = "Product name is required")
	private String name;

	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.01", message = "Price must be greater than 0")
	private BigDecimal price;

	@NotNull(message = "Available quantity is required")
	@Min(value = 0, message = "Available quantity must be at least 0")
	private Integer availableQuantity;
}
