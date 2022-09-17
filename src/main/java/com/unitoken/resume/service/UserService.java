package com.unitoken.resume.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserService {

    @Autowired
    Algorithm algorithm;

    @Autowired
    String salt;

    private Map<String, String> tokenTable = new HashMap<>();

    public String getToken(String openId) {
        String token = tokenTable.get(openId);
        if (null == token) {
            token = encode(openId, salt);
        }
        return token;
    }

    private String encode(String openId, String salt) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(now.plus(2, ChronoUnit.HOURS))
                .withClaim("id", openId)
                .withClaim("data", salt)
                .sign(algorithm);
    }
}
