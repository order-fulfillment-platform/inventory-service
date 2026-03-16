package com.ofp.inventoryservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofp.inventoryservice.dto.CreateProductRequest;
import com.ofp.inventoryservice.repository.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class InventoryControllerIntegrationTest {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

	@Container
	static ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void createProduct_shouldReturn201AndPersistProduct() throws Exception {
    	// Arrange
    	CreateProductRequest request = new CreateProductRequest();
    	request.setName("Product A");
    	request.setPrice(new BigDecimal("29.99"));
    	request.setAvailableQuantity(100);

    	// Act & Assert
    	mockMvc.perform(post("/api/v1/products")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(request)))
    			.andExpect(status().isCreated())
    			.andExpect(jsonPath("$.id").isNotEmpty())
    			.andExpect(jsonPath("$.name").value("Product A"))
    			.andExpect(jsonPath("$.price").value(29.99))
    			.andExpect(jsonPath("$.availableQuantity").value(100));

    	assertThat(productRepository.findAll()).hasSize(1);
    }

    @Test
    void getProduct_shouldReturn404WhenNotFound() throws Exception {
    	mockMvc.perform(get("/api/v1/products/" + UUID.randomUUID()))
    		.andExpect(status().isNotFound());
    }

    @Test
    void createProduct_thenGetProduct_shouldReturnSameData() throws Exception {
    	// Arrange
    	CreateProductRequest request = new CreateProductRequest();
    	request.setName("Product B");
    	request.setPrice(new BigDecimal("49.99"));
    	request.setAvailableQuantity(50);

    	// Create
    	String response = mockMvc.perform(post("/api/v1/products")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(request)))
    			.andExpect(status().isCreated())
    			.andReturn().getResponse().getContentAsString();

    	String id = objectMapper.readTree(response).get("id").asText();

    	// Get
    	mockMvc.perform(get("/api/v1/products/" + id))
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$.name").value("Product B"))
    		.andExpect(jsonPath("$.availableQuantity").value(50));
    }
}
