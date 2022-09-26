package com.unitoken.resume.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lark.oapi.Client;
import com.lark.oapi.service.contact.v3.model.Department;
import com.lark.oapi.service.contact.v3.model.GetDepartmentReq;
import com.lark.oapi.service.contact.v3.model.GetUserReq;
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
    Client client;

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

    public List<User> getAllLocalUsers() {
        return jdbcTemplate.query(
                "SELECT open_id, department_read, department_write, global_read, global_write FROM user",
                (ResultSet rs, int rowNum) -> {
                    // TODO check if result set is empty
                    return new User(
                            rs.getString("open_id"),
                            rs.getBoolean("department_read"),
                            rs.getBoolean("department_write"),
                            rs.getBoolean("global_read"),
                            rs.getBoolean("global_write")
                    );
                }
        );
    }

    public User getLocalUser(String openId) {
        var results = jdbcTemplate.query(
                "SELECT department_read, department_write, global_read, global_write FROM user WHERE open_id = ?",
                (ResultSet rs, int rowNum) -> {
                    // TODO check if result set is empty
                    return new User(
                            openId,
                            rs.getBoolean("department_read"),
                            rs.getBoolean("department_write"),
                            rs.getBoolean("global_read"),
                            rs.getBoolean("global_write")
                    );
                },
                openId
        );
        logger.info("getUser results is empty: " + results.isEmpty());
        logger.info("getUser results is null: " + (results == null));
        logger.info("getUser results.get(0): " + results.get(0).getName());
        return results.isEmpty() ? null : results.get(0);
    }

    public User getUser(String openId) throws Exception {
        com.lark.oapi.service.contact.v3.model.User larkUser = client.contact().user()
                .get(GetUserReq.newBuilder()
                        .userId(openId)
                        .build())
                .getData().getUser();
        String departmentId = larkUser.getDepartmentIds()[0];
        Department larkDepartment = client.contact().department()
                .get(GetDepartmentReq.newBuilder()
                        .departmentId(departmentId)
                        .build())
                .getData().getDepartment();

        User user = getLocalUser(openId);
        user.setName(larkUser.getName());
        user.setDepartment(larkDepartment.getName());
        return user;
    }

    public void addUser(String openId) {
        KeyHolder holder = new GeneratedKeyHolder();
        if (1 != jdbcTemplate.update(
                (conn) -> {
                    var ps = conn.prepareStatement(
                            "INSERT INTO user (open_id, department_read, department_write, global_read, global_write)" +
                                    "VALUES (?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, openId);
                    ps.setBoolean(2, true);
                    ps.setBoolean(3, false);
                    ps.setBoolean(4, false);
                    ps.setBoolean(5, false);
                    return ps;
                },
                holder
        )) {
            throw new RuntimeException("failed to insert user");
        }
    }

    public void modifyUser(User user) {
        if (1 != jdbcTemplate.update(
                (conn) -> {
                    var ps = conn.prepareStatement(
                            "UPDATE user SET department_read = ?, department_write = ?, global_read = ?, global_write = ? " +
                                    "WHERE open_id = ?");
                    ps.setBoolean(1, user.getDepartmentRead());
                    ps.setBoolean(2, user.getDepartmentWrite());
                    ps.setBoolean(3, user.getGlobalRead());
                    ps.setBoolean(4, user.getGlobalWrite());
                    ps.setString(5, user.getOpenId());
                    return ps;
                }
        )) {
            throw new RuntimeException("failed to modify user");
        }
    }

    public String authorize(String token) {
        String openId = invTokenTable.get(token);
        logger.info("token auth: " + openId);
        return openId;
    }
}
