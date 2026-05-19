package com.eatproject.backend.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider JwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        String token = resolveToken(request);

        if (token != null && JwtProvider.validate(token)) {

            String email = JwtProvider.getEmail(token);
            String role = JwtProvider.getRole(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            Collection<GrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

            Authentication auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}

//
//public class JwtFilter extends GenericFilter {
//
//    private final JwtProvider jwtProvider;
//
//    public JwtFilter(JwtProvider jwtProvider) {
//        this.jwtProvider = jwtProvider;
//    }
//
//    @Override
//    public void doFilter(ServletRequest request,
//                         ServletResponse response,
//                         FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest req = (HttpServletRequest) request;
//
//        String header = req.getHeader("Authorization");
//
//        if (header != null && header.startsWith("Bearer ")) {
//
//            String token = header.substring(7);
//
//            if (jwtProvider.validate(token)) {
//
//                String email = jwtProvider.getEmail(token);
//
//                UsernamePasswordAuthenticationToken auth =
//                        new UsernamePasswordAuthenticationToken(
//                                email,
//                                null,
//                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
//                        );
//
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        }
//
//        chain.doFilter(request, response);
//    }
//}