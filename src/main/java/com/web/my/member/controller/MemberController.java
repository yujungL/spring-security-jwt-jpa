package com.web.my.member.controller;

import com.web.my.common.bean.Response;
import com.web.my.common.bean.ResponseStatus;
import com.web.my.common.util.SecurityUtil;
import com.web.my.member.repository.MemberRepository;
import com.web.my.member.service.MemberService;
import com.web.my.member.vo.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class MemberController {

    @Autowired
    MemberService memberService;

    @GetMapping("/member/mypage")
    public String mypage(){
        return "/member/mypage.html";
    }

    @ResponseBody
    @GetMapping("/api/member/getMyInfo")
    public Response getMyInfo(HttpServletRequest request){
        Response response = null;
        try {
            String userId = String.valueOf(request.getAttribute("userId"));
            Member myInfo = memberService.getMemberInfo(userId);
            if(myInfo == null){
                response = new Response(ResponseStatus.FAILURE, "회원 정보가 없습니다.");
            }else {
                response = new Response(ResponseStatus.SUCCESS, "성공", myInfo);
            }
        }catch (Exception e){
            log.error(e.toString());
            response = new Response(ResponseStatus.FAILURE, e.getMessage());
        }

        return response;
    }

    @ResponseBody
    @PostMapping("/api/member/modify-proc")
    public Response modifyInfo(Member member){
        Response response = null;
        if(member == null){
            return new Response(ResponseStatus.FAILURE, "개인정보 수정에 실패하였습니다.");
        }

        Member result = memberService.updateMemberInfo(member);
        if(result == null){
            return new Response(ResponseStatus.FAILURE, "개인정보 수정에 실패하였습니다.");
        }else {
            return new Response(ResponseStatus.SUCCESS, "성공", result);
        }
    }

}
