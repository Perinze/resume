package com.unitoken.resume;

import com.lark.oapi.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ResumeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResumeApplication.class, args);
	}

	@Value("${config.app-id")
	String appId;

	@Value("${config.app-secret")
	String appSecret;

	@Bean
	Client createClient() {
		return Client.newBuilder(appId, appSecret)
				.logReqAtDebug(true)
				.build();
	}
}
