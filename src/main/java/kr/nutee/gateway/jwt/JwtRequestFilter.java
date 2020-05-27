package kr.nutee.gateway.jwt;


import kr.nutee.gateway.Model.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@Slf4j
public class JwtRequestFilter extends AbstractGatewayFilterFactory<Config>  {

    @Autowired
    private JwtValidator jwtValidator;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().get("Authorization").get(0).substring(7);
            log.info("token : " + token);
            Map<String, Object> userInfo = jwtValidator.getUserParseInfo(token);
            log.info("role of Request user : " + userInfo.get("role"));
            String str = (String)userInfo.get("role");
            log.info("role: " + userInfo.get("role") + userInfo.get("role").getClass());

            if(config.getRole().equals("ROLE_ADMIN")){
                if ( !str.equals("ROLE_ADMIN")) {
                    throw new IllegalArgumentException();
                }
            }

            if(config.getRole().equals("ROLE_MANAGER")){
                if ( !(config.getRole().equals("ROLE_ADMIN")
                        || config.getRole().equals("ROLE_MANAGER"))
                ) {
                    throw new IllegalArgumentException();
                }
            }

            if(config.getRole().equals("ROLE_USER")){
                if ( !(config.getRole().equals("ROLE_ADMIN")
                        || config.getRole().equals("ROLE_MANAGER")
                        || config.getRole().equals("ROLE_USER"))
                ) {
                    throw new IllegalArgumentException();
                }
            }

            return chain.filter(exchange);
        };
    }
}