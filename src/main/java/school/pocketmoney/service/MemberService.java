package school.pocketmoney.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.pocketmoney.domain.Member;
import school.pocketmoney.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자 주입
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원가입
    @Transactional
    public Member join(Member member) {
        // 1. 아이디 중복 검사
        memberRepository.findByMemberId(member.getMemberId())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 아이디입니다.");
                });

        // 3. DB에 저장
        return memberRepository.save(member);
    }

    // 로그인
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public Member login(String memberId, String password) {
        // 1. memberId로 회원 조회
        Optional<Member> memberOptional = memberRepository.findByMemberId(memberId);

        if (memberOptional.isEmpty()) {
            // 해당 ID의 회원이 없는 경우
            return null;
        }

        Member member = memberOptional.get();

        if (member.getPw().equals(password)) {
            return member; // 비밀번호 일치: 로그인 성공
        } else {
            return null; // 비밀번호 불일치: 로그인 실패
        }
    }
}
