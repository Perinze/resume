package com.unitoken.resume.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

@RestController
@RequestMapping("/test")
public class TestController {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${config.app-id}")
    String appId;
    @Value("${config.app-secret}")
    String appSecret;

    //@GetMapping("/login/common")
    @CrossOrigin(origins = "https://recruit.itoken.team")
    @PostMapping(value = "/login/common",
    consumes = "application/json;charset=UTF-8",
    produces = "application/json;charset=UTF-8")
    public void login(@RequestBody Code code) {
        logger.info("code: " + code.getCode());
    }

    private String generateUrlSignature(String secret, String timestamp) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signatureBytes = mac.doFinal(timestamp.getBytes("UTF-8"));
        String signature = new String(Base64.encodeBase64(signatureBytes));
        if("".equals(signature)) {
            return "";
        }
        String encoded = URLEncoder.encode(signature, "UTF-8");
        String urlEncodeSignature = encoded.replace("+", "%20")
                .replace("*", "%2A")
                .replace("~", "%7E")
                .replace("/", "%2F");
        return urlEncodeSignature;
    }

    private String getUserInfo(String code) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String accessKey = appId;
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = generateUrlSignature(appSecret, timestamp);

        ObjectMapper mapper = new ObjectMapper();

    }

    class Code {
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
