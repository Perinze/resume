package com.unitoken.resume;

import com.lark.oapi.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
public class ResumeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResumeApplication.class, args);
	}

	@Value("${config.app-id}")
	String appId;

	@Value("${config.app-secret}")
	String appSecret;

	@Bean
	Client createClient() {
		return Client.newBuilder(appId, appSecret)
				.logReqAtDebug(true)
				.build();
	}
	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
	    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
	    loggingFilter.setIncludeClientInfo(true);
	    loggingFilter.setIncludeQueryString(true);
	    loggingFilter.setIncludePayload(true);
	    loggingFilter.setMaxPayloadLength(64000);
	    return loggingFilter;
	}
}
