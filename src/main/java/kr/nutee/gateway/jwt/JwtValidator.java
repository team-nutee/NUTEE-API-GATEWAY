package kr.nutee.gateway.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;


@Component
public class JwtValidator implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;
    private static final Logger logger = LoggerFactory.getLogger(JwtValidator.class);

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public Map<String, Object> getUserParseInfo(String token) {
        Claims parseInfo = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        Map<String, Object> result = new HashMap<>();
        //expiration date < now
        System.out.println("만료시간"+parseInfo.getExpiration());
        boolean isExpired = parseInfo.getExpiration().before(new Date());
        result.put("username", parseInfo.getSubject());
        result.put("role", parseInfo.get("role", List.class));
        result.put("isExpired", isExpired);
        System.out.println("parseinfo in getuserparseinfo: " + result);
        return result;
    }

}
