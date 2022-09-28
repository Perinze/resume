package com.unitoken.resume.service;

import com.unitoken.resume.database.DbTemplate;
import com.unitoken.resume.model.Department;
import com.unitoken.resume.model.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MailService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DbTemplate db;

    public List<Mail> getAll() {
        List<Mail> mails = db.from(Mail.class).list();
        return mails;
    }

    public Mail getById(Long id) {
        Mail mail = db.from(Mail.class).where("id = ?", id).unique();
        return mail;
    }

    public void insertMail(Mail mail) {
        db.insert(mail);
    }

    public void modifyMail(Mail mail) {
        db.update(mail);
    }

    public void deleteMail(Long id) {
        // check if there is department using this mail template
        Department department = db.from(Department.class).where("mail_id = ?", id).first();
        if (department != null) {
            department.setMailId(1L);
            db.update(department);
        }
        db.delete(Mail.class, id);
    }

    public Mail getDepartmentMail(Long openId) {
        Long mailId = db.from(Department.class).where("open_id = ?", openId).unique().getMailId();
        Mail mail = db.from(Mail.class).where("id = ?", mailId).unique();
        return mail;
    }

    public void setDepartmentMail(Long openId, Long mailId) {
        Department department = db.from(Department.class).where("open_id = ?", openId).unique();
        department.setMailId(mailId);
        db.update(department);
    }
}
