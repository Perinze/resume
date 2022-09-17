package com.unitoken.resume.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class UserService {

    @Autowired
    Algorithm algorithm;

    @Autowired
    JWTVerifier verifier;

    @Autowired
    String salt;

    private Map<String, Deque<String>> tokenTable = new HashMap<>();

    // always return a valid token
    public String getToken(String openId) {
        String token = newestToken(openId);
        if (null == token) {
            token = encode(openId);
            pushToken(openId, token);
        }
        return token;
    }

    public String refreshToken(String token) {
        DecodedJWT jwt = decode(token);
        String openId = jwt.getClaim("id").toString();
        if (jwt.getExpiresAtAsInstant()
                .minus(30, ChronoUnit.MINUTES)
                .isBefore(Instant.now())) {
            // will expire in less than 30 min
            pushToken(openId, encode(token));
        }
        return newestToken(openId);
        /*
         * actually newly generated token can be returned
         * by override original token parameter and
         * return it. Invocation of newestToken is
         * for cleaning up.
         */
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
        } catch (JWTVerificationException exception) {
            //Invalid signature/claims
            throw new RuntimeException("invalid token");
        }
        if (jwt.getExpiresAtAsInstant().isBefore(Instant.now())) {
            throw new RuntimeException("token expired");
        }
        return jwt;
    }

    private String newestToken(String openId) {
        return getTokenDeque(openId).peekLast();
    }

    private void pushToken(String openId, String token) {
        getTokenDeque(openId).addLast(token);
    }

    private Deque<String> getTokenDeque(String openId) {
        tokenTable.putIfAbsent(openId, new LinkedList<>());
        Deque<String> q = tokenTable.get(openId);
        cleanInvalidToken(openId, q);
        return q;
    }

    private void cleanInvalidToken(String openId, Deque<String> q) {
        String token;
        while (null != (token = q.peekFirst())) {
            try {
                if (!openId.equals(decode(token).getClaim("id"))) {
                    throw new RuntimeException("token mapping error");
                }
            } catch (RuntimeException exception) {
                q.removeFirst();
            }
        }
    }
}
