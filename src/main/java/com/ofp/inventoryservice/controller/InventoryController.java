package com.ofp.inventoryservice.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ofp.inventoryservice.dto.CreateProductRequest;
import com.ofp.inventoryservice.dto.ProductResponse;
import com.ofp.inventoryservice.entity.Product;
import com.ofp.inventoryservice.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

	private final ProductRepository productRepository;

	@PostMapping
	public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
		log.info("Received create product request for {}", request.getName());

		Product product = Product.builder()
				.name(request.getName())
				.price(request.getPrice())
				.availableQuantity(request.getAvailableQuantity())
				.build();

		Product saved = productRepository.save(product);

		  return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.builder()
	                .id(saved.getId())
	                .name(saved.getName())
	                .price(saved.getPrice())
	                .availableQuantity(saved.getAvailableQuantity())
	                .build());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id) {
		log.info("Received get product request for id {}", id);
		return productRepository.findById(id)
				.map(p -> ResponseEntity.ok(ProductResponse.builder()
						.id(p.getId())
						.name(p.getName())
						.price(p.getPrice())
						.availableQuantity(p.getAvailableQuantity())
						.build()))
				.orElse(ResponseEntity.notFound().build());
	}
}
