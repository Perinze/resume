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
        var localUsers = userService.getAllUsers();
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
            user.setDepartmentId(departmentId);
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
        com.unitoken.resume.model.User user = userService.getUser(id);

        ObjectNode root = mapper.valueToTree(user);
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        return jsonString;
    }

    @PatchMapping(value = "/user/{id}",
            consumes = "application/json;charset=UTF-8")
    public void patchUser(@PathVariable String id, @RequestBody JsonNode body) throws Exception {
        var user = userService.getUser(id);
        user.setDepartmentRead(body.get("department_read") == null ? user.getDepartmentRead() : body.get("department_read").asBoolean());
        user.setDepartmentWrite(body.get("department_write") == null ? user.getDepartmentWrite() : body.get("department_write").asBoolean());
        user.setGlobalRead(body.get("global_read") == null ? user.getGlobalRead() : body.get("global_read").asBoolean());
        user.setGlobalWrite(body.get("global_write") == null ? user.getGlobalWrite() : body.get("global_write").asBoolean());
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
