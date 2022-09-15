package com.unitoken.resume.service;

import com.unitoken.resume.model.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

@Component
public class MailService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Mail> getAll() {
        return jdbcTemplate.query(
                "SELECT id, author, content FROM mail",
                (ResultSet rs, int rowNum) -> {
                    // TODO check if result set is empty
                    return new Mail(
                            rs.getLong("id"),
                            rs.getString("author"),
                            rs.getString("content")
                    );
                }
        );
    }

    public Mail getById(Long id) {
        return jdbcTemplate.query(
                "SELECT id, author, content FROM mail WHERE id = ?",
                (ResultSet rs, int rowNum) -> {
                    // TODO check if result set is empty
                    return new Mail(
                            rs.getLong("id"),
                            rs.getString("author"),
                            rs.getString("content")
                    );
                },
                id
        ).get(0);
    }

    public void insertMail(Mail mail) {
        KeyHolder holder = new GeneratedKeyHolder();
        if (1 != jdbcTemplate.update(
                (conn) -> {
                    var ps = conn.prepareStatement(
                            "INSERT INTO mail (author, content) VALUES (?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, mail.getAuthor());
                    ps.setString(2, mail.getContent());
                    return ps;
                },
                holder
        )) {
            throw new RuntimeException("failed to insert mail");
        }
        mail.setId(holder.getKey().longValue());
    }

    public void modifyMail(Mail mail) {
        if (1 != jdbcTemplate.update(
                (conn) -> {
                    var ps = conn.prepareStatement(
                            "UPDATE mail SET author = ?, content = ? WHERE id = ?");
                    ps.setString(1, mail.getAuthor());
                    ps.setString(2, mail.getContent());
                    ps.setLong(3, mail.getId());
                    return ps;
                }
        )) {
            throw new RuntimeException("failed to modify mail");
        }
    }

    public void deleteMail(Long id) {
        if (1 != jdbcTemplate.update(
                (conn) -> {
                    var ps = conn.prepareStatement(
                            "DELETE FROM mail WHERE id = ?");
                    ps.setLong(1, id);
                    return ps;
                }
        )) {
            throw new RuntimeException("failed to delete mail");
        }
    }
}
