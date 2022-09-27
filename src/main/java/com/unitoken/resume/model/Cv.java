package com.unitoken.resume.model;

import com.unitoken.resume.model.Comment;
import com.unitoken.resume.model.LocalAbstractModel;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cv")
public class Cv extends LocalAbstractModel {
    String author;
    String content;
    String state;
    String departmentId;
    Timestamp createAt;
    List<Comment> comments;

    @Column(nullable = false)
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Column(nullable = false)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(nullable = false)
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(nullable = false, name = "department_id")
    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    @Transient
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Cv() {

    }

    public Cv(String author, String departmentId, String content) {
        this.author = author;
        this.departmentId = departmentId;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Cv{" +
                "id=" + getId() +
                ", author='" + getAuthor() + '\'' +
                ", departmentId='" + getDepartmentId() + '\'' +
                ", content='" + getContent() + '\'' +
                ", state='" + getState() + '\'' +
                ", comments=" + getComments() +
                '}';
    }
}
