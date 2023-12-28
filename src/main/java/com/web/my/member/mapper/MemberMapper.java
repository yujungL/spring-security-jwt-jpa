package com.web.my.member.mapper;

import com.web.my.member.vo.Member;

public interface MemberMapper {

    public Member getMember(String userId);

    public int chkDuplicateMember(Member member);

    public int insertMember(Member member);

    public int updateRtk(Member member);

}
