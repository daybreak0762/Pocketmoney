package school.pocketmoney.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.pocketmoney.domain.Member;
import school.pocketmoney.dto.TradeRequest;
import school.pocketmoney.repository.MemberRepository;

@Service
public class TradeService {

    @Autowired
    private MemberRepository memberRepository;

    // 📌 컨트롤러에서 호출하는 그 함수입니다!
    @Transactional
    public void processTrade(TradeRequest request) {

        // 1. 현재 로그인한 사용자 찾기 (여기서는 테스트용으로 ID 'jwd11' 고정)
        // 실제로는 request에서 ID를 받거나 세션에서 가져와야 합니다.
        String memberId = "jwd11";
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 2. 거래 타입(매수/매도)에 따른 로직 수행
        if ("BUY".equalsIgnoreCase(request.getTradeType())) {
            // [매수] 로직
            int cost = request.getAmount();

            // 잔액 확인
            if (member.getProperty() < cost) {
                throw new IllegalStateException("잔액이 부족합니다.");
            }

            // 돈 차감 (property는 회원의 자산을 의미하는 필드라고 가정)
            member.setProperty(member.getProperty() - cost);

            // TODO: 나중에 여기에 '주식 보유량(Portfolio)'을 늘리는 코드를 추가해야 합니다.

        } else if ("SELL".equalsIgnoreCase(request.getTradeType())) {
            // [매도] 로직
            int earnings = request.getAmount();

            // 돈 증가
            member.setProperty(member.getProperty() + earnings);

            // TODO: 나중에 여기에 '주식 보유량'을 줄이는 코드를 추가해야 합니다.
        }

        // 3. 변경된 자산 정보를 DB에 저장 (업데이트)
        memberRepository.save(member);
    }
}