package com.unitoken.resume.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class JsonService {
    @Autowired
    ObjectMapper mapper;

    final Logger logger = LoggerFactory.getLogger(getClass());

    public String jsonString(Object object) throws JsonProcessingException {
        Serializable root = mapper.valueToTree(object);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        logger.info(json);
        return json;
    }
}
