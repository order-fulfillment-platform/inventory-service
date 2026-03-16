package com.ofp.inventoryservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

	@Bean
	NewTopic stockReservedTopic() {
		return TopicBuilder.name("stock.reserved")
				.partitions(3)
				.replicas(1)
				.build();
	}

	@Bean
	NewTopic stockRejectedTopic() {
		return TopicBuilder.name("stock.rejected")
				.partitions(3)
				.replicas(1)
				.build();
	}
}
