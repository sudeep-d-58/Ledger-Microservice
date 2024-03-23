package com.command.ledger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Ledger Command Service", version = "1.0", description = "Write Service for Ledger Application"))

public class CommandServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommandServiceApplication.class, args);
	}

}
