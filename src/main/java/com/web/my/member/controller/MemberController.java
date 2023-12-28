package com.web.my.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class MemberController {

    @GetMapping("/member/mypage")
    public String mypage(){
        return "/member/mypage.html";
    }

    @GetMapping("/api/member/getMyInfo")
    public Map getMyInfo(){
        Map<String, Object> myInfo = new HashMap<>();

        return myInfo;
    }

}
