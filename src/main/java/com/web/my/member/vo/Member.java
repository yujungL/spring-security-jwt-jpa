package com.web.my.member.vo;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;


@Data
@Getter
@Setter
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 작용 (@CreatedDate 등)
//@NoArgsConstructor
//@AllArgsConstructor
@DynamicUpdate  // 변경된 필드만 업데이트 (이거 안쓰면 빈거 전부 null 로 업데이트 됨)
@DynamicInsert  // 변경된 필드만 인서트
@Table(name = "member")
public class Member {

    @Id //primary key 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment , 기본키 생성을 데이터베이스에 위임(IDENTITY)
    private Long idx;

    private String userId;
    private String password;

    private String firstName;
    private String lastName;
    private String email;

    private String role;

    @CreatedDate
    private Timestamp regDate;

    @LastModifiedDate
    private Timestamp uptDate;

}
