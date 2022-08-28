package com.unitoken.resume.web;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    final Logger logger = LoggerFactory.getLogger(getClass());
    final RestTemplate restTemplate = new RestTemplate();
    ObjectMapper mapper = new ObjectMapper();

    @Value("${config.app-id}")
    String appId;
    @Value("${config.app-secret}")
    String appSecret;

    //@GetMapping("/login/common")
    @CrossOrigin(origins = "https://recruit.itoken.team")
    @PostMapping(value = "/login/common",
    consumes = "application/json;charset=UTF-8",
    produces = "application/json;charset=UTF-8")
    public void login(@RequestBody Code code) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        logger.info("code: " + code.getCode());

        UserInfo userInfo = getUserInfo(code.getCode());
        String openid = userInfo.openid;
        String unionid = userInfo.unionid;
        String nick = userInfo.nick;

        logger.info("user info: " + mapper.writeValueAsString(userInfo));
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

    private UserInfo getUserInfo(String code) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        String accessKey = appId;
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = generateUrlSignature(appSecret, timestamp);

        logger.info("appid: " + appId);
        logger.info("appsecret: " + appSecret);
        logger.info("timestamp: " + timestamp);

        String url = String.format(
                "https://oapi.dingtalk.com/sns/getuserinfo_bycode?accessKey=%s&timestamp=%s&signature=%s",
                accessKey, timestamp, signature);
        UserInfoResponse response = restTemplate.postForObject(
                url,
                Map.of("tmp_auth_code", code),
                UserInfoResponse.class
        );

        logger.info(mapper.writeValueAsString(response));
        return response.userInfo;

        /*
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserInfoResponse response = mapper.readValue()
         */
    }

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

class UserInfo {
    String nick;
    String unionid;
    String openid;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Boolean getMainOrgAuthHighLevel() {
        return mainOrgAuthHighLevel;
    }

    public void setMainOrgAuthHighLevel(Boolean mainOrgAuthHighLevel) {
        this.mainOrgAuthHighLevel = mainOrgAuthHighLevel;
    }

    Boolean mainOrgAuthHighLevel;
}

class UserInfoResponse {
    Long errcode;
    UserInfo userInfo;

    public Long getErrcode() {
        return errcode;
    }

    public void setErrcode(Long errcode) {
        this.errcode = errcode;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    String errmsg;
}
