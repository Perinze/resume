package com.unitoken.resume.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lark.oapi.Client;
import com.lark.oapi.core.response.RawResponse;
import com.lark.oapi.core.token.AccessTokenType;
import com.unitoken.resume.model.User;
import com.unitoken.resume.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//@CrossOrigin(origins = {"https://resume-dev.itoken.team", "http://localhost:8080"})
@CrossOrigin
@RestController
public class LoginController {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ObjectMapper mapper;

    @Autowired
    Client client;

    @Autowired
    UserService userService;

    @PostMapping(value = {"/login/common", "/login"},
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String loginHandler(@RequestBody Map<String, String> request) throws Exception, JsonMappingException {

        String code = request.get("code");

        JsonNode data = auth(code);
        String openId = data.get("open_id").asText();
        String userAccessToken = data.get("access_token").asText();
        String token = userService.getToken(openId);

        User user = userService.getUser(openId);
        if (null == user) userService.addUser(openId);

        ObjectNode root = mapper.createObjectNode();
        root.put("code", 0);
        root.put("token", token);
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        return jsonString;
    }

    @GetMapping(value = "/login/refresh",
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String refreshHandler(@RequestHeader("Authorization") String token) throws JsonProcessingException {

        ObjectNode root = mapper.createObjectNode();
        logger.info(token);
        root.put(
                "token",
                userService.refreshToken(token)
        );
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        return jsonString;
    }

    private JsonNode auth(String code) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("grant_type", "authorization_code");
        body.put("code", code);

        RawResponse resp = client.post(
                "https://open.feishu.cn/open-apis/authen/v1/access_token",
                body,
                AccessTokenType.Tenant);

        logger.info(resp.toString());

        //System.out.println(resp.getStatusCode());
        //System.out.println(Jsons.DEFAULT.toJson(resp.getHeaders()));
        JsonNode respBody = mapper.readTree(new String(resp.getBody()));
        //System.out.println(resp.getRequestID());
        logger.info(respBody.toString());

        return respBody.get("data");
    }
}
