package com.unitoken.resume.model;

public class Comment {
    Long id;
    Long cvId;
    String author;
    String content;

    public Comment(Long id, Long cvId, String author, String content) {
        this.id = id;
        this.cvId = cvId;
        this.author = author;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", cvId=" + cvId +
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
        return cvId;
    }

    public void setCv_id(Long cvId) {
        this.cvId = cvId;
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
