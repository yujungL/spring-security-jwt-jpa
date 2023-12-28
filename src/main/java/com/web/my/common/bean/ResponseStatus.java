package com.web.my.common.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor  
public enum ResponseStatus {
    SUCCESS("성공", "success", true),
    FAILURE("실패", "failure", true),
    NONEAUTH("로그인 필요", "noneauth", true);

    @Getter 
    private String desc;

    @Getter 
    private String descEng;

    @Getter 
    private boolean active;
}
