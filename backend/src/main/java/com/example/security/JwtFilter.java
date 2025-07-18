package com.example.security;

import io.quarkus.vertx.web.RouteFilter;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class JwtFilter {

//    @Inject
//    private TokenManage tokenManage;

    /**
     * 自定义过滤器
     * @param context
     */
    @RouteFilter(10000)
    public void filter(RoutingContext context) {
        String authHeader = context.request().getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            if(token.isEmpty() || "null".equals(token)){
                // 移除无效的 Authorization 头
                context.request().headers().remove(HttpHeaders.AUTHORIZATION);
            }

//            else if(!tokenManage.check(token)){
//                context.response().setStatusCode(403);
//                context.fail(403);
//                return;
//            }
//            User user = TokenManage.contextGetUser(context);

        }
        context.next();
    }
}
