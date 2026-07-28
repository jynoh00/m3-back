package com.example.community.security;

import com.example.community.common.ResponseFormat;
import com.example.community.common.ResponseMessage;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws IOException, ServletException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (isAuthPageRequest(request)) {
            handleAuthPageRequest(authorization, request, response, filterChain);
            return;
        }

        String token = extractToken(authorization);

        if (token != null && tokenProvider.validateAccessToken(token)) {
            String role = tokenProvider.getRole(token);
            Long userId = tokenProvider.getUserId(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority(role)));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthPageRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        return ("GET".equals(method) || "POST".equals(method))
                && ("/login".equals(path) || "/join".equals(path));
    }

    private void handleAuthPageRequest(String authorization,
                                       HttpServletRequest request,
                                       HttpServletResponse response,
                                       FilterChain filterChain)
            throws IOException, ServletException {

        String token = extractToken(authorization);

        if (token != null && tokenProvider.validateAccessToken(token)) {
            setAuthorizedResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        return authorization.substring(7);
    }

    private void setAuthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ResponseFormat<Map<String, String>> responseBody = ResponseFormat.of(
                ResponseMessage.ALREADY_AUTHORIZED.getMessage(),
                Map.of("redirect_url", "/posts")
        );

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/h2-console")
                || "OPTIONS".equals(request.getMethod());
    }
}