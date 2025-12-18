package com.banking;

import com.banking.dto.response.AccountResponse;
import com.banking.entity.Account;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankingSystemApplication {

	@Bean
	public ModelMapper modelMapper() {

		ModelMapper modelMapper = new ModelMapper();

		// 🔴 THIS LINE FIXES YOUR ERROR
		modelMapper.getConfiguration()
				.setAmbiguityIgnored(true);

		return modelMapper;
	}

	public static void main(String[] args) {
		SpringApplication.run(BankingSystemApplication.class, args);
	}
}
