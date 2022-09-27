package com.unitoken.resume.service;

import com.unitoken.resume.database.DbTemplate;
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
import java.util.stream.Collectors;

@Component
public class CvService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DbTemplate db;

    public List<Cv> getAll() {
        List<Cv> cvs = db.from(Cv.class).list();
        for (Cv cv : cvs) {
            cv.setComments(getComments(cv));
        }
        return cvs;
    }

    public List<Cv> getByDepartment(String departmentId) {
        return getAll().stream().filter(cv ->
            cv.getDepartmentId() == departmentId
        ).collect(Collectors.toList());
    }

    public Cv getById(Long id) {
        Cv cv = db.from(Cv.class).where("id = ?", id).unique();
        cv.setComments(getComments(cv));
        return cv;
    }

    public void insertCv(Cv cv) {
        cv.setState("unchecked");
        db.insert(cv);
    }

    public void modifyState(Long id, String state) {
        Cv cv = db.from(Cv.class).where("id = ?", id).unique();
        cv.setState(state);
        db.update(cv);
    }

    public void deleteCv(Long id) {
        db.delete(Cv.class, id);
    }

    public List<Comment> getComments(Cv cv) {
        List<Comment> comments = db.from(Comment.class).where("cv_id = ?", cv.getId()).list();
        return comments;
    }

    public void insertComment(Long cvId, Comment comment) {
        comment.setCvId(cvId);
        db.insert(comment);
    }
}
