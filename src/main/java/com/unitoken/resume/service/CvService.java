package com.unitoken.resume.service;

import com.unitoken.resume.model.Cv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class CvService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Cv> getAll() {
        return jdbcTemplate.query(
                "SELECT id, author, department, content, state FROM cv",
                (ResultSet rs, int rowNum) -> {
                    // TODO check if result set is empty
                    return new Cv(
                            rs.getLong("id"),
                            rs.getString("author"),
                            rs.getString("department"),
                            rs.getString("content"),
                            rs.getString("state")
                    );
                }
        );
    }

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

    public void insert(Cv cv) {
        KeyHolder holder = new GeneratedKeyHolder();
        if (1 != jdbcTemplate.update(
                (conn) -> {
                    var ps = conn.prepareStatement(
                            "INSERT INTO cv (author, department, content, state) VALUES (?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, cv.getAuthor());
                    ps.setString(2, cv.getDepartment());
                    ps.setString(3, cv.getContent());
                    ps.setString(4, cv.getState());
                    return ps;
                },
                holder
        )) {
            throw new RuntimeException("failed to insert cv");
        }
        cv.setId(holder.getKey().longValue());
    }
}
