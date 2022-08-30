package com.unitoken.resume.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/test")
public class TestController {

    final Logger logger = LoggerFactory.getLogger(getClass());
    final RestTemplate restTemplate = new RestTemplate();
    ObjectMapper mapper = new ObjectMapper();

}
