package kr.nutee.gateway.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;


@Component
@Slf4j
public class JwtValidator implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

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
        result.put("role", parseInfo.get("role", String.class));
        result.put("id", parseInfo.get("id", Integer.class).toString());
        result.put("isExpired", isExpired);
        System.out.println("parseInfo in getUserParseInfo: " + result);
        return result;
    }

}
