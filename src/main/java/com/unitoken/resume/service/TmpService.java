package com.unitoken.resume.service;

import com.unitoken.resume.model.TmpCv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Statement;

@Component
public class TmpService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public void insert(TmpCv tmpCv) {
        if (1 != jdbcTemplate.update(
                (conn) -> {
                    var ps = conn.prepareStatement(
                            "INSERT INTO tmp_cv " +
                                    "(birthday, college, department, dormitory, experience, grade, hometown, introduce, mail, name, nation, phone, proclass, qq, reason, sex, sno) " +
                                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    ps.setString(1, tmpCv.getBirthday());
                    ps.setString(2, tmpCv.getCollege());
                    ps.setString(3, tmpCv.getDepartment());
                    ps.setString(4, tmpCv.getDormitory());
                    ps.setString(5, tmpCv.getExperience());
                    ps.setString(6, tmpCv.getGrade());
                    ps.setString(7, tmpCv.getHometown());
                    ps.setString(8, tmpCv.getIntroduce());
                    ps.setString(9, tmpCv.getMail());
                    ps.setString(10, tmpCv.getName());
                    ps.setString(11, tmpCv.getNation());
                    ps.setString(12, tmpCv.getPhone());
                    ps.setString(13, tmpCv.getProclass());
                    ps.setString(14, tmpCv.getQq());
                    ps.setString(15, tmpCv.getReason());
                    ps.setString(16, tmpCv.getSex());
                    ps.setString(17, tmpCv.getSno());
                    return ps;
                }
        )) {
            throw new RuntimeException("failed to insert tmp cv");
        }
    }
}
