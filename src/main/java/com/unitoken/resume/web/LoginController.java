package com.unitoken.resume.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unitoken.resume.entity.User;
import com.unitoken.resume.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "https://resume-dev.itoken.team")
@RestController
@RequestMapping("/login")
public class LoginController {
    final Logger logger = LoggerFactory.getLogger(getClass());
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public String commonLoginHandler(@RequestBody Map<String, String> request) throws JsonProcessingException {
        /*
        1. extract code from request
        2. get union id by the code
        3. get user object from data source by union id
        4. generate a token (access token with some salt)
        5. return several fields of user object:
           - code: 0
           - access_token: token
           - nick: nick
           - department: department
           - scope: scope (depend on role)
         */
        String code = request.get("code");
        User user = userService.getUserByCode(code);
        // TODO generate true token
        String token = "token";
        return mapper.createObjectNode()
                .put("code", 0)
                .put("access_token", token)
                .put("nick", user.getNick())
                .put("department", user.getDepartment().toString())
                .put("scope", user.getRole().toString())
                .asText();
    }
}
