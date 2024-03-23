package com.query.ledger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Ledger Query Service", version = "1.0", description = "Query Service for Ledger Application"))
public class LedgerQueryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LedgerQueryServiceApplication.class, args);
	}

}
