package com.github.sidd6p.store.filters;

import com.github.sidd6p.store.services.Jwt;
import com.github.sidd6p.store.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");

        // If no Authorization header, continue filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = authHeader.replace("Bearer ", "");
        Jwt jwt = jwtService.parseToken(token);

        // Only set authentication if JWT is valid
        // If invalid, continue filter chain and let Spring Security handle authorization
        if (jwt != null && jwt.isValid()) {
            var authentication = new UsernamePasswordAuthenticationToken(
                    jwt.getUserId(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + jwt.getRole()))
            );
            // Set the authentication in the SecurityContext
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Always continue the filter chain - let Spring Security decide if authentication is required
        filterChain.doFilter(request, response);
    }
}
