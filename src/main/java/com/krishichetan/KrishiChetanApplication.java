package com.krishichetan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(exclude = {org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration.class})
public class KrishiChetanApplication {

	public static void main(String[] args) {
		SpringApplication.run(KrishiChetanApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
