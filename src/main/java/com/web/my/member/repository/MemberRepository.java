package com.web.my.member.repository;

import com.web.my.member.vo.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    //findBy+컬럼명 > 컬럼 조건으로 select
    public Member findByUserId(String userId);
    public Member findByUserIdAndPassword(String userId, String password);


    void flush(); //캐시에 저장했다가 실제 DB에 커밋할 때 적용하는 거?


}
