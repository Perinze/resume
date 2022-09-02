package com.unitoken.resume.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.print.attribute.standard.Media;
import java.util.Map;

@Component
public class LarkService {

    @Value("${config.app-id}")
    String appId;
    @Value("${config.app-secret}")
    String appSecret;

    String token;

    final RestTemplate restTemplate = new RestTemplate();
    final ObjectMapper mapper = new ObjectMapper();

    final Logger logger = LoggerFactory.getLogger(getClass());

    public void updateTenantAccessToken() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode body = mapper.createObjectNode();
        body.put("app_id", appId);
        body.put("app_secret", appSecret);

        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
        var response = restTemplate.postForEntity(
                "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal",
                request, String.class);
        JsonNode responseNode = mapper.readTree(response.getBody());

        if (0 != responseNode.get("code").asInt()) {
            throw new RuntimeException("TenantAccessToken: " + responseNode.get("msg").asText());
        }

        token = responseNode.get("tenant_access_token").asText();
        logger.info("Tenant Access Token updated");
    }

    public JsonNode getUserAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        ObjectNode body = mapper.createObjectNode();
        body.put("grant_type", "authorization_code");
        body.put("code", code);

        logger.info(body.toString());
        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
        var response = restTemplate.postForEntity(
                "https://open.feishu.cn/open-apis/authen/v1/access_token",
                request, String.class);
        JsonNode responseNode = mapper.readTree(response.getBody());

        if (0 != responseNode.get("code").asInt()) {
            throw new RuntimeException("UserAccessToken: " + responseNode.get("msg").asText());
        }

        return responseNode.get("data");
    }

    public JsonNode refreshUserAccessToken(String refreshToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        ObjectNode body = mapper.createObjectNode();
        body.put("grant_type", "authorization_code");
        body.put("refresh_token", refreshToken);

        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
        var response = restTemplate.postForEntity(
                "https://open.feishu.cn/open-apis/authen/v1/refresh_access_token",
                request, String.class);
        JsonNode responseNode = mapper.readTree(response.getBody());

        if (0 != responseNode.get("code").asInt()) {
            throw new RuntimeException("UserAccessToken: " + responseNode.get("msg").asText());
        }

        return responseNode.get("data");
    }

    public JsonNode getUserInfo(String userAccessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + userAccessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        var response = restTemplate.exchange(
                "https://open.feishu.cn/open-apis/authen/v1/user_info",
                HttpMethod.GET, request, String.class);
        JsonNode responseNode = mapper.readTree(response.getBody());

        if (0 != responseNode.get("code").asInt()) {
            throw new RuntimeException("UserAccessToken: " + responseNode.get("msg").asText());
        }

        return responseNode.get("data");
    }

    public JsonNode getContactUserInfo(String unionId, String userAccessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        logger.info("Bearer " + token);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<>(headers);
        var response = restTemplate.exchange(
                "https://open.feishu.cn/open-apis/contact/v3/users/{id}?user_id_type={type}",
                HttpMethod.GET, request, String.class,
                unionId, "union_id");
        JsonNode responseNode = mapper.readTree(response.getBody());

        if (0 != responseNode.get("code").asInt()) {
            throw new RuntimeException("UserAccessToken: " + responseNode.get("msg").asText());
        }

        return responseNode.get("data");
    }
}
