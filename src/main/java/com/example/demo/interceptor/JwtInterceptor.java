package com.example.demo.interceptor;

import com.example.demo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 请求前置拦截：从Authorization头提取Bearer token，校验有效性和角色权限
     * 校验通过后将用户信息存入request属性，后续Controller可直接读取
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (!jwtUtil.isTokenExpired(token)) {
                Claims claims = jwtUtil.parseToken(token);
                String role = claims.get("role", String.class);
                String path = request.getRequestURI();
                // 角色权限校验：管理员接口只能ADMIN访问，医生接口只能DOCTOR访问
                if (path.startsWith("/api/admin/") && !"ADMIN".equals(role)) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"无权限访问\"}");
                    return false;
                }
                if (path.startsWith("/api/doctor/") && !"DOCTOR".equals(role)) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"无权限访问\"}");
                    return false;
                }
                request.setAttribute("userId", claims.get("userId", Long.class));
                request.setAttribute("username", claims.get("username", String.class));
                request.setAttribute("role", role);
                return true;
            }
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\"}");
        return false;
    }
}
