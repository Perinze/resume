package com.unitoken.resume.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lark.oapi.Client;
import com.lark.oapi.service.contact.v3.model.Department;
import com.lark.oapi.service.contact.v3.model.GetDepartmentReq;
import com.lark.oapi.service.contact.v3.model.GetUserReq;
import com.lark.oapi.service.contact.v3.model.User;
import com.unitoken.resume.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    Client client;

    Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping(value = "/users",
            produces = "application/json;charset=UTF-8")
    public String getAllUser() throws Exception {
        var localUsers = userService.getAllLocalUsers();
        for (var user : localUsers) {
            String id = user.getOpenId();
            User larkUser = client.contact().user()
                    .get(GetUserReq.newBuilder()
                            .userId(id)
                            .build())
                    .getData().getUser();
            String departmentId = larkUser.getDepartmentIds()[0];
            Department larkDepartment = client.contact().department()
                    .get(GetDepartmentReq.newBuilder()
                            .departmentId(departmentId)
                            .build())
                    .getData().getDepartment();
            user.setName(larkUser.getName());
            user.setDepartment(larkDepartment.getName());
        }

        ArrayNode root = mapper.valueToTree(localUsers);
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        logger.info(jsonString);
        return jsonString;
    }

    @GetMapping(value = "/user/{id}",
            produces = "application/json;charset=UTF-8")
    public String getUser(@PathVariable String id) throws Exception {
        User larkUser = client.contact().user()
                .get(GetUserReq.newBuilder()
                        .userId(id)
                        .build())
                .getData().getUser();
        String departmentId = larkUser.getDepartmentIds()[0];
        Department larkDepartment = client.contact().department()
                .get(GetDepartmentReq.newBuilder()
                        .departmentId(departmentId)
                        .build())
                .getData().getDepartment();
        com.unitoken.resume.model.User localUser = userService.getLocalUser(id);

        ObjectNode root = mapper.createObjectNode();
        root.put("openid", id)
                .put("name", larkUser.getName())
                .put("department", larkDepartment.getName())
                .put("department_read", localUser.getDepartmentRead())
                .put("department_write", localUser.getDepartmentWrite())
                .put("global_read", localUser.getGlobalRead())
                .put("global_write", localUser.getGlobalWrite());
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        return jsonString;
    }

    @PatchMapping(value = "/user/{id}",
            consumes = "application/json;charset=UTF-8")
    public void patchUser(@PathVariable String id, @RequestBody JsonNode body) {
        var user = new com.unitoken.resume.model.User(
                id,
                body.get("department_read").asBoolean(),
                body.get("department_write").asBoolean(),
                body.get("global_read").asBoolean(),
                body.get("global_write").asBoolean()
        );
        userService.modifyUser(user);
    }

    @GetMapping(value = "/auth",
            produces = "application/json;charset=UTF-8")
    public void auth(@RequestHeader Map<String, String> header) {
        String token = header.get("authorization");
        logger.info("token: " + token);
        String openId = userService.authorize(token);
        logger.info(openId);
    }
}
