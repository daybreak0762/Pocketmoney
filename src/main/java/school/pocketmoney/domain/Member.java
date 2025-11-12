package school.pocketmoney.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity // 이 클래스가 JPA 엔티티임을 나타냅니다.
@Table(name = "member") // 실제 테이블 이름 (확인 필요)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자
public class Member {

    @Id // 기본 키(Primary Key) 지정
    @Column(name = "memberId", nullable = false, length = 255)
    private String memberId; // varchar(255), NO NULL

    @Column(name = "name", length = 45)
    private String name; // varchar(45)

    @Column(name = "email", length = 255)
    private String email; // varchar(255)

    @Column(name = "pw", nullable = false, length = 255)
    private String pw; // varchar(255), 비밀번호 필드

    // 나머지 필드는 입력받지 않지만, DB 스키마에 맞게 추가
    // 간단한 구성을 위해 @Builder 패턴을 사용하지 않고 생성자를 이용해 필수값만 초기화합니다.
    private Long ranking; // bigint
    private Date waypoint; // date
    private Integer pt; // int
    private Boolean ban; // tinyint(1)
    private Long property; // bigint
    private Boolean ad; // tinyint(1)

    // 회원가입 시 필수 입력 값만 받는 생성자
    @Builder
    public Member(String memberId, String name, String email, String pw) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.pw = pw;

        // 나머지 필드들은 기본값 설정
        this.ranking = 0L;
        this.waypoint = new Date(2015, 1, 1); // 예시: 현재 시간
        this.pt = 0;
        this.ban = false;
        this.property = 10000000L;
        this.ad = false;
    }

    // 관리자 확인 메서드
    public boolean isAdmin() {
        // ad 필드가 true이면 관리자로 간주
        return this.ad != null && this.ad;
    }
}