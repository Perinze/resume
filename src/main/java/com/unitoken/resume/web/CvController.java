package com.unitoken.resume.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unitoken.resume.model.Cv;
import com.unitoken.resume.service.CvService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
public class CvController {

    @Autowired
    CvService cvService;

    @Autowired
    ObjectMapper mapper;

    Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping(value = "/cvs",
            produces = "application/json;charset=UTF-8")
    public String getCv() throws JsonProcessingException {
        List<Cv> cvs = cvService.getAll();
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
        consumes = "application/json;charset=UTF-8",
        produces = "application/json;charset=UTF-8")
    public void postCv(@RequestBody JsonNode cvNode) {
        logger.info("post cv");
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
}
