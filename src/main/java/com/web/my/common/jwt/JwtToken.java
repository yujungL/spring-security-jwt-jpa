package com.web.my.common.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtToken {
    private String grantType;       //JWT 인증타입
    private String accessToken;
    private String refreshToken;
}
