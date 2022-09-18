package com.unitoken.resume.model;

public class User {
    String openId;
    String name;
    String department;
    Boolean departmentRead;
    Boolean departmentWrite;
    Boolean globalRead;
    Boolean globalWrite;

    public User(String openId, Boolean departmentRead, Boolean departmentWrite, Boolean globalRead, Boolean globalWrite) {
        this.openId = openId;
        this.departmentRead = departmentRead;
        this.departmentWrite = departmentWrite;
        this.globalRead = globalRead;
        this.globalWrite = globalWrite;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Boolean getDepartmentRead() {
        return departmentRead;
    }

    public void setDepartmentRead(Boolean departmentRead) {
        this.departmentRead = departmentRead;
    }

    public Boolean getDepartmentWrite() {
        return departmentWrite;
    }

    public void setDepartmentWrite(Boolean departmentWrite) {
        this.departmentWrite = departmentWrite;
    }

    public Boolean getGlobalRead() {
        return globalRead;
    }

    public void setGlobalRead(Boolean globalRead) {
        this.globalRead = globalRead;
    }

    public Boolean getGlobalWrite() {
        return globalWrite;
    }

    public void setGlobalWrite(Boolean globalWrite) {
        this.globalWrite = globalWrite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
