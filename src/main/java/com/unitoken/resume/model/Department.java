package com.unitoken.resume.model;

public class Department {
    Long id;
    String openId;
    Long mailId;

    public Department(Long id, String openId, Long mailId) {
        this.id = id;
        this.openId = openId;
        this.mailId = mailId;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", openId='" + openId + '\'' +
                ", mailId=" + mailId +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getMailId() {
        return mailId;
    }

    public void setMailId(Long mailId) {
        this.mailId = mailId;
    }
}
