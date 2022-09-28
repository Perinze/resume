package com.unitoken.resume.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "department")
public class Department extends SyncAbstractModel {
    Long mailId;

    public Department() {

    }

    public Department(String openId, Long mailId) {
        super.setOpenId(openId);
        this.mailId = mailId;
    }

    @Override
    public String toString() {
        return "Department{" +
                ", openId='" + super.getOpenId() + '\'' +
                ", mailId=" + this.getMailId() +
                '}';
    }

    @Column(nullable = false, name = "mail_id")
    public Long getMailId() {
        return mailId;
    }

    public void setMailId(Long mailId) {
        this.mailId = mailId;
    }
}
