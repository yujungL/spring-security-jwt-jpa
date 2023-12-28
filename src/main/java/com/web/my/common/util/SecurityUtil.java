package com.web.my.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class SecurityUtil {

    private SecurityUtil(){}

    // JwtFilter 클래스의 doFilter 메소드에서 저장한 Security Context의 인증 정보에서 username을 리턴
    // 유저 정보에서 UserId 만 반환하는 메소드
    public static Map getCurrentMember() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> resultMap = new HashMap<>();
        if(authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }

        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        resultMap.put("clientIp", String.valueOf(details == null? "" : details.getRemoteAddress()));
        resultMap.put("userId", String.valueOf(authentication.getName()));
        resultMap.put("role", authorities);
        resultMap.put("credential", String.valueOf(authentication.getCredentials()));
        resultMap.put("principal", String.valueOf(authentication.getPrincipal()));

        return resultMap;

    }

}
