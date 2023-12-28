package com.web.my.common.jwt;

import com.web.my.common.bean.Role;
import com.web.my.member.mapper.MemberMapper;
import com.web.my.member.repository.MemberRepository;
import com.web.my.member.service.MemberDetailService;
import com.web.my.member.vo.Member;
import com.web.my.member.vo.MemberDetail;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor //@Autowired 해야하는거 자동으로 해주는?
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Autowired
    MemberRepository memberRepository;

    MemberDetailService memberDetailService;
    private Key secretKey;
    private final long exp = 1000L * 60 * 60; //만료시간 1H

    @PostConstruct  //@Autowired 한거 생성자 필요할 때
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    // security 에서 만든 객체에 담긴 유저 정보로 토큰 생성
    public JwtToken createToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        //Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("role", authorities)
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        //Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("role", authorities)
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        
        //Refresh Token DB 저장
        Member member = memberRepository.findByUserId(authentication.getName());
        member.setRtk(refreshToken);
        memberRepository.save(member);

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // JWT 토큰을 복호화하여 토큰에있는 인증 정보 조회
    public Authentication getAuthentication(String token){
        //토큰 복호화
        Claims claims = parseClaims(token);
        if(claims.get("role") == null){
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // token으로 사용자 id 조회
    public String getLoginIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getId);
    }

    // token으로 사용자 속성정보 조회
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 모든 token에 대한 사용자 속성정보 조회
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    // Request의 Header에서 token 값 조회 "authorization" : "token'
    public String resolveToken(HttpServletRequest request) {
        if(request.getHeader("authorization") != null )
            return request.getHeader("authorization").substring(7);
        return null;
    }

    // 토큰 정보를 검증하는 메소드
    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        }catch (SecurityException | MalformedJwtException e){
            log.info("Invalid JWT Token : {}", e);
        }catch (ExpiredJwtException e){
            log.info("Expired JWT Token : {}", e);
        }catch (UnsupportedJwtException e){
            log.info("Unsupported JWT Token : {}", e);
        }catch (IllegalArgumentException e){
            log.info("JWT claims string is empty : {}", e);
        }

        return false;
    }

    // 토큰 복호화
    public Claims parseClaims(String token){
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

}
