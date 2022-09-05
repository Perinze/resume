package com.unitoken.resume.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.unitoken.resume.entity.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DepartmentService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    LarkService larkService;

    Logger logger = LoggerFactory.getLogger(getClass());

    public void getDepartmentById(String departmentId, String accessToken) throws JsonProcessingException {
        JsonNode data = larkService.getDepartment(departmentId, accessToken);
        String name = data.get("name").asText();
        logger.info(name);
        /*
        Department department = Department.valueOf(name);
        return department;
         */
    }
}
