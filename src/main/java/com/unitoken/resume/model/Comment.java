package com.unitoken.resume.model;

public class Comment {
    Long id;
    Long cv_id;
    String author;
    String content;

    public Comment(Long id, Long cv_id, String author, String content) {
        this.id = id;
        this.cv_id = cv_id;
        this.author = author;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", cv_id=" + cv_id +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCv_id() {
        return cv_id;
    }

    public void setCv_id(Long cv_id) {
        this.cv_id = cv_id;
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
}
