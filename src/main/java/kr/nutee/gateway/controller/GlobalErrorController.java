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
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
@Order(-2)
public class GlobalErrorController implements ErrorWebExceptionHandler {
    @Override
    @NonNull
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable throwable) {
        log.warn("in GATEWAY Exeptionhandler : " + throwable);
        String errorMessage = "";
        if (throwable.getClass() == NullPointerException.class) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            errorMessage = "NullPointerException";
        } else if (throwable.getClass() == ExpiredJwtException.class) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            errorMessage = "JWT TOKEN이 만료되었습니다.";
        } else if (throwable.getClass() == MalformedJwtException.class || throwable.getClass() == SignatureException.class || throwable.getClass() == UnsupportedJwtException.class) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            errorMessage = "JWT TOKEN의 형식에 오류가 있습니다.";
        } else if (throwable.getClass() == IllegalArgumentException.class) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            errorMessage = "해석된 TOKEN의 데이터에 오류가 있습니다.";
        }

        byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}