package school.pocketmoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.pocketmoney.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    // memberId를 이용해 회원을 찾는 메서드 (로그인 시 사용)
    Optional<Member> findByMemberId(String memberId);
}
