package com.web.my.login.service;

import com.web.my.common.bean.Role;
import com.web.my.common.jwt.JwtToken;
import com.web.my.common.jwt.JwtTokenProvider;
import com.web.my.common.util.SecurityUtil;
import com.web.my.member.mapper.MemberMapper;
import com.web.my.member.repository.MemberRepository;
import com.web.my.member.vo.Member;
import com.web.my.member.vo.MemberDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.swing.text.html.Option;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class LoginService {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    public LoginService(JwtTokenProvider jwtTokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public JwtToken login(String userId, String password){
        //1. Authentication 객체 생성
        //이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, password);

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 MemberDetailService 에서 만든 loadUserByUsername 메서드가 실행
        // 로그인 실패 시 >> BadCredentialsException: 자격 증명에 실패하였습니다.
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //검증된 인증 정보로 JWT토큰 생성
        JwtToken token = jwtTokenProvider.createToken(authentication);

        return token;
    }

    @Transactional
    public JwtToken register(Member member){
        Member duplicate = memberRepository.findByUserId(member.getUserId());
        if(duplicate != null){
            throw new RuntimeException("이미 가입되어있는 유저입니다.");
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setRole(Role.USER.name());
        Member insertData = memberRepository.save(member);

        if(insertData != null){
            return this.login(insertData.getUserId(), insertData.getPassword());
        }else {
            throw new RuntimeException("가입 실패.");
        }
    }

    @Transactional(readOnly = true)
    public Boolean loginCheck() {
        Map<String, Object> resultMap = SecurityUtil.getCurrentMember();
        if(!Role.ANONYMOUS.getKey().equals(resultMap.get("role"))){
            return true;
        }else {
            return false;
        }
    }

    // 현재 SecurityContext 에 있는 Member 정보 가져오기
    @Transactional(readOnly = true)
    public Map getLoginInfo() {
        Map<String, Object> resultMap = SecurityUtil.getCurrentMember();
        if(!Role.ANONYMOUS.getKey().equals(resultMap.get("role"))){
            Member memberInfo = memberRepository.findByUserId(resultMap.get("userId").toString());
            resultMap.put("firstName", memberInfo.getFirstName());
            resultMap.put("lastName", memberInfo.getLastName());
            resultMap.put("email", memberInfo.getEmail());
        }
        return resultMap;
    }

    // jwt token 재발급
    public JwtToken reIssueAccessToken(HttpServletRequest request){
        Map<String, Object> resultMap = SecurityUtil.getCurrentMember();
        String userId = resultMap.get("userId").toString();

        String savedRtk = memberRepository.findByUserId(userId).getRtk();
        if(savedRtk == null || "".equals(savedRtk)){
            return null;
        }

        String rft = (request.getAttribute("refreshToken") != null ? request.getAttribute("refreshToken").toString() : "");
        if(savedRtk.equals(rft)){
            Authentication authentication = jwtTokenProvider.getAuthentication(savedRtk);
            JwtToken token = jwtTokenProvider.createToken(authentication);
            return token;
        }else {
            return null;
        }
    }


}
