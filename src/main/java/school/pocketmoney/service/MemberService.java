package school.pocketmoney.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.pocketmoney.domain.Member;
import school.pocketmoney.repository.MemberRepository;

import java.util.List;
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

    @Transactional(readOnly = true)
    public Member getMemberById(String memberId) {
        // memberRepository.findByMemberId는 Optional<Member>를 반환합니다.
        return memberRepository.findByMemberId(memberId)
                // Optional이 비어있다면(회원이 없다면) 예외를 발생시킵니다.
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<Member> findAllMemberOrderByPropertyDesc() {
        return memberRepository.findAllByOrderByPropertyDesc();
    }

    // 전체 회원 목록 조회
    public List<Member> findAllMembers() {
        // [TODO] MemberRepository를 사용하여 DB에서 모든 Member 엔티티를 List 형태로 조회하여 반환하는 로직 구현
        return memberRepository.findAll();
    }

    // 2. 회원 차단/차단 해제 상태 업데이트
    @Transactional
    public void updateBanStatus(String memberId, boolean isBanned) {
        // 1. userId로 회원 엔티티를 조회
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID(" + memberId + ")의 회원을 찾을 수 없습니다."));

        // 2. 'ban' 필드를 받아온 isBanned 값으로 업데이트
        member.updateBanStatus(isBanned);

    }
}
