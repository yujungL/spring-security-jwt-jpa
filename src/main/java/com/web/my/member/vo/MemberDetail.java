package com.web.my.member.vo;

import com.web.my.common.bean.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MemberDetail implements UserDetails {
    // Spring Security는 유저 인증과정에서 UserDetails를 참조하여 인증을 진행
    // UserDetails 를 상속해서 MemberDetail로 인증 진행하게 설정

    private final Member member;

    public MemberDetail(Member member) {
        this.member = member;
    }

    public final Member getMember(){
        return member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /*return member.getRoles().stream().map(o -> new SimpleGrantedAuthority(
                o.getValue()
        )).collect(Collectors.toList());*/
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return member.getRole();
            }
        });
        return null;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberDetail that = (MemberDetail) o;
        return Objects.equals(member.getIdx(), that.getMember().getIdx());
    }

    @Override
    public int hashCode() {
        return Objects.hash(member.getIdx());
    }
}
