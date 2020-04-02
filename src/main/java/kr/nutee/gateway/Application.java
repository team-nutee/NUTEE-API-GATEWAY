package kr.nutee.gateway;

import kr.nutee.gateway.jwt.JwtRequestFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.cloud.gateway.route.RouteLocator;

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.HashMap;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public CorsConfiguration corsConfiguration(RoutePredicateHandlerMapping routePredicateHandlerMapping) {
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        Arrays.asList(HttpMethod.OPTIONS, HttpMethod.PUT, HttpMethod.GET, HttpMethod.DELETE, HttpMethod.POST) .forEach(m -> corsConfiguration.addAllowedMethod(m));
        corsConfiguration.addAllowedOrigin("*");
        routePredicateHandlerMapping.setCorsConfigurations(new HashMap<String, CorsConfiguration>() {{ put("/**", corsConfiguration); }});
        return corsConfiguration;
    }



    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtRequestFilter jwtRequestFilter) {
        String authServer="http://localhost:9708/";
        String feedServer="http://localhost:9425/";
        return builder.routes()
                .route("auth",  r-> r.path("/auth/**")
                        .filters(f -> f
                                .rewritePath("/auth/(?<segment>.*)", "/auth/${segment}")
                                .hystrix(config -> config
                                .setName("fallbackpoint")
                                .setFallbackUri("forward:/fallback")))
                        .uri(authServer))
                .route("follow",  r-> r.path("/follow/**")
                        .filters(f -> f
                                .rewritePath("/follow/(?<segment>.*)", "/follow/${segment}")
                                .filter(jwtRequestFilter.apply(new JwtRequestFilter.Config("ROLE_USER")))
                                .hystrix(config -> config
                                .setName("fallbackpoint")
                                .setFallbackUri("forward:/fallback")))
                        .uri(feedServer))
                .route("user", r->r.path("/user/**")
                    .filters(f -> f
                            .rewritePath("/user/(?<segment>.*)", "/user/${segment}")
                            .filter(jwtRequestFilter.apply(new JwtRequestFilter.Config("ROLE_USER"))
                    ))
                        .uri(authServer)
                )
                .route("admin", r->r.path("/admin/**")
                        .filters(f -> f
                                .rewritePath("/admin/(?<segment>.*)", "/admin/${segment}")
                                .filter(jwtRequestFilter.apply(new JwtRequestFilter.Config("ROLE_ADMIN"))
                                ))
                        .uri(authServer)
                )
                .build();
    }
}
