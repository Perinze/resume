package com.unitoken.resume.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unitoken.resume.model.Mail;
import com.unitoken.resume.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.MissingFormatArgumentException;

@CrossOrigin
@RestController
public class MailController {

    @Autowired
    MailService mailService;

    @Autowired
    ObjectMapper mapper;

    Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping(value = "/mails",
            produces = "application/json;charset=UTF-8")
    public String getMails() throws JsonProcessingException {
        List<Mail> mails = mailService.getAll();
        ArrayNode root = mapper.valueToTree(mails);
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        logger.info(jsonString);
        return jsonString;
    }

    @GetMapping(value = "/mail/{id}",
            produces = "application/json;charset=UTF-8")
    public String getMail(@PathVariable Long id) throws JsonProcessingException {
        logger.info("id=" + id);
        Mail mail = mailService.getById(id);
        ObjectNode root = mapper.valueToTree(mail);
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        logger.info(jsonString);
        return jsonString;
    }

    @PostMapping(value = "/mail",
            consumes = "application/json;charset=UTF-8")
    public void postMail(@RequestBody JsonNode mailNode) {
        Mail mail = new Mail(
                0L,
                mailNode.get("author").asText(),
                mailNode.get("content").asText()
        );
        mailService.insertMail(mail);
        logger.info(mail.toString());
    }

    @PutMapping(value = "/mail/{id}",
            consumes = "application/json;charset=UTF-8")
    public void putMail(@PathVariable Long id, @RequestBody JsonNode mailNode) {
        Mail mail = new Mail(
                id,
                mailNode.get("author").asText(),
                mailNode.get("content").asText()
        );
        mailService.modifyMail(mail);
    }

    @DeleteMapping(value = "/mail/{id}")
    public void deleteMail(@PathVariable Long id) {
        mailService.deleteMail(id);
    }
}
