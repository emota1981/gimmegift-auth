package br.com.gimmegift.auth;

import org.springframework.boot.SpringApplication;

public class TestGimmegiftAuthApplication {

	public static void main(String[] args) {
		SpringApplication.from(GimmegiftAuthApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
