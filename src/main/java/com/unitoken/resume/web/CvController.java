package com.unitoken.resume.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unitoken.resume.context.UserContext;
import com.unitoken.resume.database.DbTemplate;
import com.unitoken.resume.database.Where;
import com.unitoken.resume.exception.BadRequest;
import com.unitoken.resume.exception.NotFound;
import com.unitoken.resume.exception.PermissionDenied;
import com.unitoken.resume.model.Comment;
import com.unitoken.resume.model.Cv;
import com.unitoken.resume.model.User;
import com.unitoken.resume.service.CvService;
import com.unitoken.resume.service.JsonService;
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
    JsonService jsonService;

    @Autowired
    UserContext userContext;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    DbTemplate db;

    Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping(value = "/cvs",
            produces = "application/json;charset=UTF-8")
    public String getCv(@RequestParam Map<String, String> params) throws Exception {
        User user = userContext.getUser();

        var from = db.from(Cv.class);
        Where<Cv> where = null;

        // permission check
        if (user.getGlobalRead()) {
            logger.info("global read -> getting all cvs");

            where = from.where("true");
        } else if (user.getDepartmentRead()) {
            logger.info("department read -> getting department cvs");
            logger.info("department id: " + user.getDepartmentId());

            where = from.where("department_id = ?", user.getDepartmentId());
        } else {
            throw new PermissionDenied();
        }

        // date check
        String after = params.get("after");
        if (after != null) {
            where = where.where("create_at > ?", Timestamp.valueOf(after));
        }
        String before = params.get("before");
        if (before != null) {
            where = where.where("create_at < ?", Timestamp.valueOf(before));
        }

        List<Cv> cvs = where.list();
        for (Cv cv : cvs) {
            cv.setComments(cvService.getComments(cv));
        }
        return jsonService.jsonString(cvs);
    }

    @GetMapping(value = "/cv/{id}",
            produces = "application/json;charset=UTF-8")
    public String getCv(@PathVariable Long id) throws JsonProcessingException {
        User user = userContext.getUser();

        var from = db.from(Cv.class);
        Where<Cv> where = from.where("id = ?", id);

        // permission check
        if (user.getGlobalRead()) {
            logger.info("global read -> getting all cvs");

            where = where.where("true");
        } else if (user.getDepartmentRead()) {
            logger.info("department read -> getting department cvs");
            logger.info("department id: " + user.getDepartmentId());

            where = where.where("department_id = ?", user.getDepartmentId());
        } else {
            throw new PermissionDenied();
        }

        Cv cv = where.first();
        if (cv != null) {
            cv.setComments(cvService.getComments(cv));
        }

        return jsonService.jsonString(cv);
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
        User user = userContext.getUser();

        // TODO check state as enum

        // check if cv exists
        Cv cv = db.from(Cv.class).where("id = ?", id).first();
        if (cv == null) {
            throw new NotFound();
        }

        // check permission
        if (user.getGlobalWrite()) {

        } else if (user.getDepartmentWrite()) {
            if (!user.getDepartmentId().equals(cv.getDepartmentId())) {
                throw new PermissionDenied();
            }
        } else {
            throw new PermissionDenied();
        }

        JsonNode node = cvNode.get("state");
        if (node == null) {
            throw new BadRequest();
        }
        cv.setState(node.asText());
        db.update(cv);
    }

    @DeleteMapping(value = "/cv/{id}")
    public void deleteCv(@PathVariable Long id) {
        User user = userContext.getUser();

        // check if cv exists
        Cv cv = db.from(Cv.class).where("id = ?", id).first();
        if (cv == null) {
            throw new NotFound();
        }

        // check permission
        if (user.getGlobalWrite()) {

        } else if (user.getDepartmentWrite()) {
            if (!user.getDepartmentId().equals(cv.getDepartmentId())) {
                throw new PermissionDenied();
            }
        } else {
            throw new PermissionDenied();
        }

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
