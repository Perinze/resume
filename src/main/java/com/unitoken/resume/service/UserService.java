package com.unitoken.resume.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.unitoken.resume.database.DbTemplate;
import com.unitoken.resume.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.sql.ResultSet;
import java.sql.Statement;
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

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DbTemplate db;

    Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, Deque<String>> tokenTable = new HashMap<>();
    private Map<String, String> invTokenTable = new HashMap<>();

    // always return a valid token
    public String getToken(String openId) {
        String token = newestToken(openId);
        logger.info(token);
        if (null == token) {
            token = encode(openId);
            pushToken(openId, token);
        }
        logger.info("getToken " + getTokenDeque(openId).isEmpty());
        logger.info("token " + token);
        return token;
    }

    public String refreshToken(String token) {
        DecodedJWT jwt = decode(token);
        String openId = jwt.getClaim("id").asString();
        logger.info("extract openid " + openId + "from token");
        if (jwt.getExpiresAtAsInstant()
                .minus(30, ChronoUnit.MINUTES)
                .isBefore(Instant.now())) {
            // will expire in less than 30 min
            logger.info("refresh token");
            pushToken(openId, encode(token));
        }
        logger.info("refreshToken " + getTokenDeque(openId).isEmpty());
        String newToken = newestToken(openId);
        logger.info(newToken);
        return newToken;
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
        var q = getTokenDeque(openId);
        q.addLast(token);
        invTokenTable.put(token, openId);
        logger.info("pushToken " + q.isEmpty());
    }

    private Deque<String> getTokenDeque(String openId) {
        tokenTable.putIfAbsent(openId, new LinkedList<>());
        Deque<String> q = tokenTable.get(openId);
        cleanInvalidToken(openId, q);
        logger.info("getTokenDeque " + q.isEmpty());
        return q;
    }

    private void cleanInvalidToken(String openId, Deque<String> q) {
        String token;
        while (null != (token = q.peekFirst())) {
            try {
                if (!openId.equals(decode(token).getClaim("id").asString())) {
                    logger.info("openId: " + openId);
                    logger.info("extracted: " + decode(token).getClaim("id").asString());
                    logger.error("openId != extracted");
                    throw new RuntimeException("token mapping error");
                } else break;
            } catch (RuntimeException exception) {
                logger.error(exception.getMessage());
                q.removeFirst();
                invTokenTable.remove(token, openId);
            }
            logger.info("looping");
        }
    }

    public List<User> getAllUsers() {
        List<User> users = db.from(User.class).list();
        return users;
    }

    public User getUser(String openId) {
        User user = db.from(User.class).where("open_id = ?", openId).first();
        return user;
    }

    public void addUser(String openId) {
        User user = new User(openId, true, false, false, false);
        db.insert(user);
    }

    public void modifyUser(User user) {
        db.update(user);
    }

    public String authorize(String token) {
        String openId = invTokenTable.get(token);
        logger.info("token auth: " + openId);
        return openId;
    }
}
