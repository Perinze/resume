package com.unitoken.resume.service;

import com.unitoken.resume.model.Comment;
import com.unitoken.resume.model.Cv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

@Component
public class CvService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Cv> getAll() {
        List<Cv> cvs = jdbcTemplate.query(
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
        for (Cv cv : cvs) {
            cv.setComments(getComments(cv));
        }
        return cvs;
    }

    public Cv getById(Long id) {
        Cv cv = jdbcTemplate.query(
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
        cv.setComments(getComments(cv));
        return cv;
    }

    public void insertCv(Cv cv) {
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

    public void modifyState(Long id, String state) {
        if (1 != jdbcTemplate.update(
                (conn) -> {
                    var ps = conn.prepareStatement(
                            "UPDATE cv SET state = ? WHERE id = ?");
                    ps.setString(1, state);
                    ps.setLong(2, id);
                    return ps;
                }
        )) {
            throw new RuntimeException("failed to modify cv state");
        }
    }

    public void deleteCv(Long id) {
        if (1 != jdbcTemplate.update(
                (conn) -> {
                    var ps = conn.prepareStatement(
                            "DELETE FROM cv WHERE id = ?");
                    ps.setLong(1, id);
                    return ps;
                }
        )) {
            throw new RuntimeException("failed to delete cv");
        }
    }

    public List<Comment> getComments(Cv cv) {
        return jdbcTemplate.query(
                "SELECT id, cv_id, author, content FROM comment WHERE cv_id = ?",
                (ResultSet rs, int rowNum) -> {
                    // TODO check if result set is empty
                    return new Comment(
                            rs.getLong("id"),
                            rs.getLong("cv_id"),
                            rs.getString("author"),
                            rs.getString("content")
                    );
                },
                cv.getId()
        );
    }

    public void insertComment(Long cvId, Comment comment) {
        KeyHolder holder = new GeneratedKeyHolder();
        if (1 != jdbcTemplate.update(
                (conn) -> {
                    var ps = conn.prepareStatement(
                            "INSERT INTO comment (cv_id, author, content) VALUES (?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, cvId);
                    ps.setString(2, comment.getAuthor());
                    ps.setString(3, comment.getContent());
                    return ps;
                },
                holder
        )) {
            throw new RuntimeException("failed to insert comment");
        }
        comment.setId(holder.getKey().longValue());
    }
}
