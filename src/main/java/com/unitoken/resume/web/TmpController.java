package com.unitoken.resume.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unitoken.resume.model.TmpCv;
import com.unitoken.resume.service.TmpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "https://join.itoken.team")
@RestController
public class TmpController {
    @Autowired
    TmpService tmpService;
    @Autowired
    ObjectMapper mapper;
    Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping(value = "/cv/apply",
            consumes = "application/json;charset=UTF-8")
    public void apply(@RequestBody TmpCv tmpCv) {
        tmpService.insert(tmpCv);
    }
}
