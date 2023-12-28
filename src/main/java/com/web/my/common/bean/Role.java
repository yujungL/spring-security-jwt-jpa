package com.web.my.common.bean;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER", "회원"),
    ADMIN("ROLE_ADMIN", "관리자"),
    ANONYMOUS("ROLE_ANONYMOUS", "비회원");

    private final String key;
    private final String value;
}
