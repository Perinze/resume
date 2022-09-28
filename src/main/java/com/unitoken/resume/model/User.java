package com.unitoken.resume.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "user")
public class User extends SyncAbstractModel {
    String name;
    String departmentId;
    Boolean departmentRead;
    Boolean departmentWrite;
    Boolean globalRead;
    Boolean globalWrite;

    public User(String openId, Boolean departmentRead, Boolean departmentWrite, Boolean globalRead, Boolean globalWrite) {
        super.setOpenId(openId);
        this.departmentRead = departmentRead;
        this.departmentWrite = departmentWrite;
        this.globalRead = globalRead;
        this.globalWrite = globalWrite;
    }

    @Column(nullable = false)
    public Boolean getDepartmentRead() {
        return departmentRead;
    }

    public void setDepartmentRead(Boolean departmentRead) {
        this.departmentRead = departmentRead;
    }

    @Column(nullable = false)
    public Boolean getDepartmentWrite() {
        return departmentWrite;
    }

    public void setDepartmentWrite(Boolean departmentWrite) {
        this.departmentWrite = departmentWrite;
    }

    @Column(nullable = false)
    public Boolean getGlobalRead() {
        return globalRead;
    }

    public void setGlobalRead(Boolean globalRead) {
        this.globalRead = globalRead;
    }

    @Column(nullable = false)
    public Boolean getGlobalWrite() {
        return globalWrite;
    }

    public void setGlobalWrite(Boolean globalWrite) {
        this.globalWrite = globalWrite;
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartment(String departmentId) {
        this.departmentId = departmentId;
    }
}
