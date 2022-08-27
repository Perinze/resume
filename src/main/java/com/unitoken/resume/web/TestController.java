package com.unitoken.resume.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    final Logger logger = LoggerFactory.getLogger(getClass());


    //@GetMapping("/login/common")
    @CrossOrigin(origins = "https://recruit.itoken.team")
    @PostMapping(value = "/login/common",
    consumes = "application/json;charset=UTF-8",
    produces = "application/json;charset=UTF-8")
    public void login(@RequestBody Code code) {
        logger.info("code: " + code.getCode());
    }
}

class Code {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}