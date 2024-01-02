package com.web.my.member.service;

import com.web.my.member.repository.MemberRepository;
import com.web.my.member.vo.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MemberService {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Member getMemberInfo(String userId){
        return memberRepository.findByUserId(userId);
    }

    @Transactional
    public Member updateMemberInfo(Member member){
        Member oriMem = memberRepository.findByUserId(member.getUserId().toString());
        if(oriMem != null){
            //dirty checking
            oriMem.setFirstName(member.getFirstName());
            oriMem.setLastName(member.getLastName());
            oriMem.setPassword(passwordEncoder.encode(member.getPassword()));
            oriMem.setEmail(member.getEmail());
            return oriMem;
        }else {
            return null;
        }
    }

}
