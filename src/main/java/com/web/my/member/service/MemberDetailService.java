package com.web.my.member.service;

import com.web.my.member.mapper.MemberMapper;
import com.web.my.member.repository.MemberRepository;
import com.web.my.member.vo.Member;
import com.web.my.member.vo.MemberDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.Optional;

@Component
@Slf4j
public class MemberDetailService implements UserDetailsService {
    // UserDetailsService : Security 에서 사용하는 UserDetails 정보를 토대로 유저정보를 불러오는 서비스
    // 상속하여 MemberDetailService 에서 구현
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(userId);
        if(member != null){
            log.info("Success find member {}", member);
            log.info("member.getRole() : {}", member.getRole());

            return User.builder()
                .username(member.getUserId())
                .password(member.getPassword())
                .roles(member.getRole()) // DB 에 저장된 값에 ROLE_ 붙이면 에러남 // " cannot start with ROLE_ (it is automatically added)"
                .build();
        }else{
            throw new UsernameNotFoundException("가입되어있지 않은 계정입니다.");
        }
    }

}
