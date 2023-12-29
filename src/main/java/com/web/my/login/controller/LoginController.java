package com.web.my.login.controller;

import com.web.my.common.bean.Response;
import com.web.my.common.jwt.JwtToken;
import com.web.my.common.jwt.JwtTokenProvider;
import com.web.my.login.service.LoginService;
import com.web.my.member.vo.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class LoginController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final LoginService loginService;
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    @GetMapping("/login")
    public String loginPage(){
        return "/login/login.html";
    }

    @GetMapping("/register")
    public String registerPage(){
        return "/login/register.html";
    }

    @ResponseBody
    @PostMapping("/login")
    public Map login(@RequestParam Map<String, Object> param){
        Map<String, Object> returnMap = new HashMap<>();
        ResponseEntity<JwtToken> re = this.loginProc(param);
        if(HttpStatus.OK.equals(re.getStatusCode())){
            returnMap.put("accessToken", re.getBody().getAccessToken());
            returnMap.put("refreshToken", re.getBody().getRefreshToken());
        }
        return returnMap;
    }

    @ResponseBody
    @PostMapping("/api/login/login-proc")
    public ResponseEntity<JwtToken> loginProc(@RequestParam Map<String, Object> param){
        if(param.get("userId") == null || param.get("password") == null){
            return ResponseEntity.badRequest().build();
        }
        String userId = String.valueOf(param.get("userId"));
//        String password = passwordEncoder.encode(String.valueOf(param.get("password")));
        String password = String.valueOf(param.get("password"));
        JwtToken token = loginService.login(userId, password);
        return ResponseEntity.ok(token);
    }

    @ResponseBody
    @PostMapping("/api/login/logout-proc")
    public Response logoutProc(HttpServletRequest request, @RequestBody Map<String, Object> param){
        String accessToken = jwtTokenProvider.resolveToken(request);
        String refreshToken = param.get("refreshToken").toString();

        return loginService.logout(accessToken, refreshToken);
    }

    @ResponseBody
    @PostMapping("/api/login/register-proc")
    public Response registerProc(HttpServletRequest request, Member member){
        if(member == null){
            return null;
        }
        return loginService.register(member);
    }

    @ResponseBody
    @GetMapping("/api/login/login-check")
    public Boolean loginCheck(){
        return loginService.loginCheck();
    }

    @ResponseBody
    @GetMapping("/api/login/login-info")
    public Map loginInfo(){
        if(loginCheck()){
            return loginService.getLoginInfo();
        }
        return null;
    }

    @ResponseBody
    @PostMapping("/api/login/reissue")
    public ResponseEntity<JwtToken> tokenReIssue(HttpServletRequest request){
        String refreshToken = (request.getAttribute("refreshToken") != null ? request.getAttribute("refreshToken").toString() : "");
        JwtToken token = loginService.reIssueAccessToken(refreshToken);
        return ResponseEntity.ok(token);
    }
}
