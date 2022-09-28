package com.unitoken.resume.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unitoken.resume.database.DbTemplate;
import com.unitoken.resume.database.Where;
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

import java.sql.Timestamp;
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

    @Autowired
    DbTemplate db;

    Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping(value = "/cvs",
            produces = "application/json;charset=UTF-8")
    public String getCv(@RequestHeader Map<String, String> headers, @RequestParam Map<String, String> params) throws Exception {
        var from = db.from(Cv.class);
        Where<Cv> where = from.where("true");

        String token = headers.get("authorization");
        if (token == null) {
            throw new PermissionDenied();
        }
        User user = userService.getUser(userService.authorize(token));
        if (user.getGlobalRead()) {
            logger.info("global read -> getting all cvs");
        } else if (user.getDepartmentRead()) {
            logger.info("department read -> getting department cvs");
            logger.info("department id: " + user.getDepartmentId());
            where = where.where("department_id = ?", user.getDepartmentId());
        } else {
            throw new PermissionDenied();
        }

        String after = params.get("after");
        if (after != null) {
            where = where.where("create_at > ?", Timestamp.valueOf(after));
        }
        String before = params.get("before");
        if (before != null) {
            where = where.where("create_at < ?", Timestamp.valueOf(before));
        }

        List<Cv> cvs = where.list();
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
                cvNode.get("author").asText(),
                cvNode.get("department_id").asText(),
                cvNode.get("content").asText()
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
                id,
                commentNode.get("author").asText(),
                commentNode.get("content").asText()
        );
        cvService.insertComment(id, comment);
    }
}
