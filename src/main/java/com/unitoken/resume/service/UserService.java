package com.unitoken.resume.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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
    JWTVerifier verifier;

    @Autowired
    String salt;

    private Map<String, String> tokenTable = new HashMap<>();

    // always return a valid token
    public String getToken(String openId) {
        String token = tokenTable.get(openId);
        if (null == token) {
            token = encode(openId);
        } else {
            try {
                if (openId.equals(decode(token).getClaim("id"))) {
                    throw new RuntimeException("token mapping error");
                }
            } catch (RuntimeException exception) {
                String newToken = encode(openId);
                tokenTable.replace(openId, token, newToken);
                token = newToken;
            }
        }
        return token;
    }

    private String encode(String openId) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(now.plus(2, ChronoUnit.HOURS))
                .withClaim("id", openId)
                .withClaim("data", salt)
                .sign(algorithm);
    }

    private DecodedJWT decode(String token) {
        DecodedJWT jwt;
        try {
            jwt = verifier.verify(token);
        } catch (JWTVerificationException exception){
            //Invalid signature/claims
            throw new RuntimeException("invalid token");
        }
        if (jwt.getExpiresAtAsInstant().isBefore(Instant.now())) {
            throw new RuntimeException("token expired");
        }
        return jwt;
    }
}
