package com.unitoken.resume.web;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.response.RawResponse;
import com.lark.oapi.core.token.AccessTokenType;
import com.lark.oapi.service.contact.v3.enums.GetDepartmentDepartmentIdTypeEnum;
import com.lark.oapi.service.contact.v3.enums.GetUserUserIdTypeEnum;
import com.lark.oapi.service.contact.v3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "https://resume-dev.itoken.team")
@RestController
@RequestMapping("/login")
public class LoginController {

    final Logger logger = LoggerFactory.getLogger(getClass());

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    Client client;

    @PostMapping(value = "/",
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String loginHandler(@RequestBody Map<String, String> request) throws Exception, JsonMappingException {

        String code = request.get("code");

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

        JsonNode data = respBody.get("data");

        String openId = data.get("open_id").asText();
        String userAccessToken = data.get("access_token").asText();

        ObjectNode root = mapper.createObjectNode();
        root.put("token", userAccessToken);
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        return jsonString;

        /*
        GetUserResp getUserResp = client.contact().user().get(
                GetUserReq.newBuilder()
                        .userId(openId)
                        .userIdType(GetUserUserIdTypeEnum.OPEN_ID)
                        .build());

        User user = getUserResp.getData().getUser();
        String nick = user.getName();
        String departmentId = user.getDepartmentIds()[0];

        GetDepartmentResp getDepartmentResp = client.contact().department().get(
                GetDepartmentReq.newBuilder()
                        .departmentId(departmentId)
                        .departmentIdType(GetDepartmentDepartmentIdTypeEnum.OPEN_DEPARTMENT_ID)
                        .build(),
                RequestOptions.newBuilder()
                        .userAccessToken(userAccessToken)
                        .build()
                );

        logger.info("%d".formatted(getDepartmentResp.getCode()));
        logger.info(getDepartmentResp.getMsg());

        Department department = getDepartmentResp.getData().getDepartment();
        String departmentName = department.getName();
        logger.info(departmentName);

        ObjectNode root = mapper.createObjectNode();
        root.put("code", 0).put("access_token", "token").put("nick", nick).put("department", departmentName).put("scope", "user");
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        logger.info(jsonString);
        return jsonString;
         */
    }

    @GetMapping(value = "/update",
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateInfoHandler() throws Exception, JsonMappingException {
        ObjectNode root = mapper.createObjectNode();
        root.put("code", 0)
                .put("nick", "王超睿")
                .put("department", "技术部")
                .put("scope", "user");
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        logger.info(jsonString);
        return jsonString;
    }
}
