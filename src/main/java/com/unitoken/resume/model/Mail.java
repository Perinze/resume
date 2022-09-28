package com.unitoken.resume.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "mail")
public class Mail extends LocalAbstractModel {
    String author;
    String content;

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

    public Mail() {

    }

    public Mail(String author, String content) {
        this.author = author;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Mail{" +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
