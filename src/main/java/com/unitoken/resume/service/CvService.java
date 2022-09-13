package com.unitoken.resume.service;

import com.unitoken.resume.model.Cv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;

@Component
public class CvService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Cv getById(Long id) {
        return jdbcTemplate.query(
                "SELECT id, author, department, content, state FROM cv WHERE id = ?",
                (ResultSet rs, int rowNum) -> {
                    // TODO check if result set is empty
                    return new Cv(
                            rs.getLong("id"),
                            rs.getString("author"),
                            rs.getString("department"),
                            rs.getString("content"),
                            rs.getString("state")
                    );
                },
                id
        ).get(0);
    }
}
