package com.unitoken.resume;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
public class ResumeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResumeApplication.class, args);
	}

	@Value("${config.app-id}")
	String appId;

	@Value("${config.app-secret}")
	String appSecret;

	@Value("${config.algo-secret}")
	String algoSecret;

	@Bean
	Client createClient() {
		return Client.newBuilder(appId, appSecret)
				.logReqAtDebug(true)
				.build();
	}
	@Bean
	CommonsRequestLoggingFilter requestLoggingFilter() {
	    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
	    loggingFilter.setIncludeClientInfo(true);
	    loggingFilter.setIncludeQueryString(true);
	    loggingFilter.setIncludePayload(true);
	    loggingFilter.setMaxPayloadLength(64000);
	    return loggingFilter;
	}

	@Bean
	ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	Algorithm algorithm() {
		return Algorithm.HMAC256(algoSecret);
	}

	@Bean
	String salt() {
		return new SimpleDateFormat("HH+mm/ss&MM~dd").format(new Date());
	}

	@Bean
	JWTVerifier verifier(@Autowired Algorithm algorithm, @Autowired String salt) {
		return JWT.require(algorithm)
				.withClaim("data", salt)
				.build();
	}
}
