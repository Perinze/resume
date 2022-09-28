package com.unitoken.resume.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class SyncAbstractModel {

    private String openId;

    @Id
    @Column(nullable = false, updatable = false, name = "open_id")
    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
