package com.unitoken.resume.model;

import java.util.ArrayList;
import java.util.List;

public class Cv {
    Long id;
    String author;
    String department;
    String content;
    String state;
    List<String> comments;

    public Cv(Long id, String author, String department, String content, String state, List<String> comments) {
        this.id = id;
        this.author = author;
        this.department = department;
        this.content = content;
        this.state = state;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public Cv(Long id, String author, String department, String content, String state) {
        this.id = id;
        this.author = author;
        this.department = department;
        this.content = content;
        this.state = state;
        this.comments = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Cv{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", department='" + department + '\'' +
                ", content='" + content + '\'' +
                ", state='" + state + '\'' +
                ", comments=" + comments +
                '}';
    }
}
