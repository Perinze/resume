package com.unitoken.resume.model;

public class Department {
    String openId;
    Long mailId;

    public Department(Long id, String openId, Long mailId) {
        this.openId = openId;
        this.mailId = mailId;
    }

    @Override
    public String toString() {
        return "Department{" +
                ", openId='" + openId + '\'' +
                ", mailId=" + mailId +
                '}';
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
