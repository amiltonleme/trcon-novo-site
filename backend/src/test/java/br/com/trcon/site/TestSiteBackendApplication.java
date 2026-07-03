package br.com.trcon.site;

import org.springframework.boot.SpringApplication;

public class TestSiteBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(SiteBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
