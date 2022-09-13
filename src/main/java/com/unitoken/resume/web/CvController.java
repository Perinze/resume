package com.unitoken.resume.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unitoken.resume.model.Cv;
import com.unitoken.resume.service.CvService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "https://resume-dev.itoken.team")
@RestController
@RequestMapping("/cv")
public class CvController {

    @Autowired
    CvService cvService;

    @Autowired
    ObjectMapper mapper;

    Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping(value = "{id}",
            produces = "application/json;charset=UTF-8")
    public String getCv(@PathVariable Long id) throws JsonProcessingException {
        logger.info("id=" + id);
        Cv cv = cvService.getById(id);
        ObjectNode root = (ObjectNode)mapper.valueToTree(cv);
        root.putArray("comments");
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        logger.info(jsonString);
        return jsonString;
    }
}
