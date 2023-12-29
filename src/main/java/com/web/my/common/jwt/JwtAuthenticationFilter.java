package com.web.my.common.jwt;

import com.web.my.common.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, RedisUtil redisUtil) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisUtil = redisUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //헤더에서 JWT토큰 추출
        String token = resolveToken((HttpServletRequest) request);

        //토큰 유효성 검사
        if(token != null && jwtTokenProvider.validateToken(token)){
            String loginId = jwtTokenProvider.getLoginIdFromToken(token);
            String isLogout = "";
            if(loginId != null){
                // Redis에 해당 accessToken logout 여부를 확인
                isLogout = redisUtil.getValues(token);
            }

            // 로그아웃이 없는(되어 있지 않은) 경우 해당 토큰은 정상적으로 작동하기
            if (ObjectUtils.isEmpty(isLogout)) {
                //토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                request.setAttribute("userId", loginId);
                request.setAttribute("auth", authentication.getAuthorities().stream().collect(Collectors.toList()));
            }
        }
        chain.doFilter(request, response);
    }

    // 헤더에서 토큰 추출
    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")){ // Authorization 이 Bearer 확인
            return bearerToken.substring(7);
        }
        return null;
    }

}
