package com.unitoken.resume.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.unitoken.resume.entity.Department;
import com.unitoken.resume.entity.Role;
import com.unitoken.resume.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.Statement;


@Component
public class UserService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    LarkService larkService;

    RowMapper<User> userRowMapper = (ResultSet rs, int rowNum) -> {
        return new User(); // TODO implement later
    };


    public User getUserByCode(String code) throws JsonProcessingException {
        JsonNode data = larkService.getUserAccessToken(code);
        int unionId = data.get("union_id").asInt();
        User userOrNull = jdbcTemplate.queryForObject(
                "SELECT id, nick, department, role FROM user WHERE unionid=?",
                (ResultSet rs, int rowNum) -> {
                    if (rowNum == 0) {
                        return null;
                    }
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setNick(rs.getString("nick"));
                    user.setDepartment(Department.valueOf(rs.getString("department")));
                    user.setRole(Role.valueOf(rs.getString("role")));
                    return user;
                }
        );
        // new user
        if (userOrNull == null) {
            User user = new User();
            user.setNick(data.get("name").asText());
            user.setDepartment(larkService.getDepartment(unionId));
            user.setRole(Role.USER);

            KeyHolder holder = new GeneratedKeyHolder();
            if (1 != jdbcTemplate.update(
                    (conn) -> {
                        var ps = conn.prepareStatement(
                                "INSERT INTO user (nick, unionid, department, role) VALUES (?, ?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS);
                        ps.setObject(1, user.getNick());
                        ps.setObject(2, unionId);
                        ps.setObject(3, user.getDepartment().toString());
                        ps.setObject(4, user.getRole().toString());
                        return ps;
                    },
                    holder
            )) {
                throw new RuntimeException("Failed to insert user");
            }
            user.setId(holder.getKeyAs(Long.class));
            return user;
        } else {
            return userOrNull;
        }
    }
}
