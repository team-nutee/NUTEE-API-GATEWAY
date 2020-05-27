package kr.nutee.gateway.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
@Order(-2)
public class GlobalErrorController implements ErrorWebExceptionHandler {

    private String statusCodeMaker(int statusCode) {
        return "{\"statusCode\":" + statusCode +"}";
    }

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        log.warn("in GATEWAY Exeptionhandler : " + ex);
        int statusCode = 10;
        if (ex.getClass() == NullPointerException.class) {
            statusCode = 61;
        } else if (ex.getClass() == ExpiredJwtException.class) {
            statusCode = 56;
        } else if (ex.getClass() == MalformedJwtException.class
                || ex.getClass() == SignatureException.class
                || ex.getClass() == UnsupportedJwtException.class
        ) {
            statusCode = 55;
        } else if (ex.getClass() == IllegalArgumentException.class) {
            statusCode = 51;
        }

        byte[] bytes = statusCodeMaker(statusCode).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }
}