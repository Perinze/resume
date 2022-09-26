package com.unitoken.resume.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unitoken.resume.exception.PermissionDenied;
import com.unitoken.resume.model.Comment;
import com.unitoken.resume.model.Cv;
import com.unitoken.resume.model.User;
import com.unitoken.resume.service.CvService;
import com.unitoken.resume.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class CvController {

    @Autowired
    CvService cvService;
    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper mapper;

    Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping(value = "/cvs",
            produces = "application/json;charset=UTF-8")
    public String getCv(@RequestHeader Map<String, String> headers) throws JsonProcessingException {
        User user = userService.getUser(userService.authorize(headers.get("authorization")));
        List<Cv> cvs;
        if (user.getGlobalRead()) {
            logger.info("global read -> getting all cvs");
            cvs = cvService.getAll();
        } else if (user.getDepartmentRead()) {
            logger.info("department read -> getting department cvs");
            cvs = cvService.getByDepartment(user.getDepartment());
        } else {
            throw new PermissionDenied();
        }
        ArrayNode root = mapper.valueToTree(cvs);
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        logger.info(jsonString);
        return jsonString;
    }

    @GetMapping(value = "/cv/{id}",
            produces = "application/json;charset=UTF-8")
    public String getCv(@PathVariable Long id) throws JsonProcessingException {
        logger.info("id=" + id);
        Cv cv = cvService.getById(id);
        ObjectNode root = mapper.valueToTree(cv);
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        logger.info(jsonString);
        return jsonString;
    }

    @PostMapping(value = "/cv",
        consumes = "application/json;charset=UTF-8")
    public void postCv(@RequestBody JsonNode cvNode) {
        Cv cv = new Cv(
                0L,
                cvNode.get("author").asText(),
                cvNode.get("department").asText(),
                cvNode.get("content").asText(),
                "unchecked"
        );
        cvService.insertCv(cv);
        logger.info(cv.toString());
    }

    @PatchMapping(value = "/cv/{id}",
            consumes = "application/json;charset=UTF-8")
    public void patchCv(@PathVariable Long id, @RequestBody JsonNode cvNode) {
        // TODO check state as enum
        String state = cvNode.get("state").asText();
        cvService.modifyState(id, state);
    }

    @DeleteMapping(value = "/cv/{id}")
    public void deleteCv(@PathVariable Long id) {
        cvService.deleteCv(id);
    }

    @PostMapping(value = "/cv/{id}/comment",
            consumes = "application/json;charset=UTF-8")
    public void postComment(@PathVariable Long id, @RequestBody JsonNode commentNode) {
        Comment comment = new Comment(
                0L,
                id,
                commentNode.get("author").asText(),
                commentNode.get("content").asText()
        );
        cvService.insertComment(id, comment);
    }
}
