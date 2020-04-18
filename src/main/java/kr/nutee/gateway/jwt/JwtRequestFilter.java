package kr.nutee.gateway.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import kr.nutee.gateway.Model.Config;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
public class JwtRequestFilter extends AbstractGatewayFilterFactory<Config>  {

    @Autowired
    private JwtValidator jwtValidator;

    public JwtRequestFilter() {
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().get("Authorization").get(0).substring(7);
            log.info("token : " + token);
            Map<String, Object> userInfo = jwtValidator.getUserParseInfo(token);
            log.info("role of Request user : " + userInfo.get("role"));
            String str = (String)userInfo.get("role");
            log.info("role: " + userInfo.get("role") + userInfo.get("role").getClass());
            if ( !str.contains(config.getRole())) {
                throw new IllegalArgumentException();
            }
            return chain.filter(exchange);
        };
    }
}