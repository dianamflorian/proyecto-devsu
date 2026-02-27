package com.devsu.movimientos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableJpaRepositories(basePackages = "com.devsu.movimientos.repository")
public class MsMovimientosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsMovimientosApplication.class, args);
	}

}