package com.unitoken.resume;

import com.larksuite.oapi.core.AppSettings;
import com.larksuite.oapi.core.Config;
import com.larksuite.oapi.core.DefaultStore;
import com.larksuite.oapi.core.Domain;
import com.larksuite.oapi.service.bitable.v1.model.App;
import com.larksuite.oapi.service.contact.v3.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ResumeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResumeApplication.class, args);
	}

	@Bean
	AppSettings createAppSettings() {
		return Config.createInternalAppSettings(
				"cli_a384ab309178d00c",
				"wiZiveYzG1acpHXkzYwZWdvBumqmk3EL",
				"ecurn8n2UnacW19JBFJZbeKnL2bSSaGU",
				"WWPz55VRaZ08tYNIvvjB8flXqS08tN1E");
	}

	@Bean
	Config createConfig(@Autowired AppSettings appSettings) {
		return new Config(Domain.FeiShu, appSettings, new DefaultStore());
	}

	@Bean
	ContactService createContactService(@Autowired Config config) {
		return new ContactService(config);
	}
}
