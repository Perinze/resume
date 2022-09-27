package com.unitoken.resume.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "comment")
public class Comment extends LocalAbstractModel {
    Long cvId;
    String author;
    String content;

    @Column(nullable = false, name = "cv_id")
    public Long getCvId() {
        return cvId;
    }

    public void setCvId(Long cvId) {
        this.cvId = cvId;
    }

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

    public Comment() {

    }

    public Comment(Long cvId, String author, String content) {
        this.cvId = cvId;
        this.author = author;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + super.getId() +
                ", cvId=" + getCvId() +
                ", author='" + getAuthor() + '\'' +
                ", content='" + getAuthor() + '\'' +
                '}';
    }
}
