package com.unitoken.resume.service;

import com.unitoken.resume.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;


public class UserService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    RowMapper<User> userRowMapper = (ResultSet rs, int rowNum) -> {
        return new User(); // TODO implement later
    };
}
