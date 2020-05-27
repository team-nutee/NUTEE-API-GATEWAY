package kr.nutee.gateway.Route;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import kr.nutee.gateway.Model.Config;
import kr.nutee.gateway.jwt.JwtRequestFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

@Controller
public class Router {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtRequestFilter jwtRequestFilter) {
        String authServer = "http://localhost:9708/";
        String snsServer = "http://localhost:9425/";
        String crawlServer = "http://localhost:9709/";
        return builder.routes()
                .route("auth", r -> r.path("/auth/**")
                        .filters(f -> f
                                .rewritePath("/auth/(?<segment>.*)", "/auth/${segment}")
                                .hystrix(config -> config
                                        .setName("fallbackpoint")
                                        .setFallbackUri("forward:/fallback")))
                        .uri(authServer))
                .route("crawl", r -> r.path("/crawl/**")
                        .filters(f -> f
                                .rewritePath("/crawl/(?<segment>.*)", "/crawl/${segment}")
                                .hystrix(config -> config
                                        .setName("fallbackpoint")
                                        .setFallbackUri("forward:/fallback")))
                        .uri(crawlServer))
                .route("sns", r -> r.path("/sns/**")
                        .filters(f -> f
                                .rewritePath("/sns/(?<segment>.*)", "/sns/${segment}")
                                .filter(jwtRequestFilter.apply(new Config("ROLE_USER")))
                                .hystrix(config -> config
                                        .setName("fallbackpoint")
                                        .setFallbackUri("forward:/fallback")))
                        .uri(snsServer))
                .route("user", r -> r.path("/user/**")
                        .filters(f -> f
                                .rewritePath("/user/(?<segment>.*)", "/user/${segment}")
                                .filter(jwtRequestFilter.apply(new Config("ROLE_USER"))
                                ))
                        .uri(authServer)
                )
                .route("admin", r -> r.path("/admin/**")
                        .filters(f -> f
                                .rewritePath("/admin/(?<segment>.*)", "/admin/${segment}")
                                .filter(jwtRequestFilter.apply(new Config("ROLE_ADMIN"))
                                ))
                        .uri(authServer)
                )
                .build();
    }
}
