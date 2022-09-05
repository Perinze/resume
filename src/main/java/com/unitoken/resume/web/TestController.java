package com.unitoken.resume.web;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unitoken.resume.service.LarkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@CrossOrigin(origins = "https://resume-dev.itoken.team")
@RestController
@RequestMapping("/")
public class TestController {
    final Logger logger = LoggerFactory.getLogger(getClass());
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    LarkService larkService;

    @GetMapping("/hello")
    public String hello() {
        return "hello, world!";
    }

    @GetMapping("/token")
    public void getToken() throws JSONException, JsonProcessingException {
        larkService.updateTenantAccessToken();
    }

    @PostMapping("/login/common1")
    public void login(@RequestBody Map<String, String> req) throws JSONException, JsonProcessingException {
        larkService.updateTenantAccessToken();

        String code = req.get("code");
        logger.info(code);
        JsonNode data = larkService.getUserAccessToken(code);
        String userAccessToken = data.get("access_token").asText();
        logger.info(userAccessToken);
        String openId = data.get("open_id").asText();
        String unionId = data.get("union_id").asText();

        JsonNode userinfo = larkService.getContactUserInfo(unionId);
        logger.info(userinfo.toString());
    }
}
