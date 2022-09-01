package com.unitoken.resume.web;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Value("${config.app-id}")
    String appId;
    @Value("${config.app-secret}")
    String appSecret;

    final Logger logger = LoggerFactory.getLogger(getClass());
    final RestTemplate restTemplate = new RestTemplate();
    ObjectMapper mapper = new ObjectMapper();

    String token;

    void updateAppAccessToken() throws JSONException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject body = new JSONObject();
        body.put("app_id", appId);
        body.put("app_secret", appSecret);
        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);

        var res = restTemplate.postForEntity(
                "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal",
                request, String.class);
        JsonNode root = mapper.readTree(res.getBody());
        token = root.get("tenant_access_token").toString();
        logger.info(token);
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello, world!";
    }

    @GetMapping("/token")
    public void getToken() throws JSONException, JsonProcessingException {
        updateAppAccessToken();
    }

    @PostMapping("/login/common")
    public void login(@RequestBody Map<String, String> req) {
        logger.info("/login/common");
        logger.info(req.get("code"));
    }
}
