package school.pocketmoney.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.pocketmoney.domain.Member;
import school.pocketmoney.domain.Portfolio;
import school.pocketmoney.domain.Stock;
import school.pocketmoney.repository.MemberRepository;
import school.pocketmoney.repository.PortfolioRepository;
import school.pocketmoney.repository.StockRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeService {

    private final MemberRepository memberRepository;
    private final StockRepository stockRepository;       // 주가 확인용
    private final PortfolioRepository portfolioRepository; // 보유량 기록용

    // 📌 [매수] count = "몇 주"를 살 것인가
    public void buyStock(String memberId, Long companyId, int count) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        // 1. 주가 테이블(Stock)에서 해당 회사의 '현재 가격' 조회
        Stock stockInfo = stockRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("이 회사의 주가 정보가 없습니다."));

        int currentPrice = stockInfo.getCurrentPrice();

        // 2. 총 필요한 돈 계산 (가격 x 수량)
        long totalCost = (long) currentPrice * count;

        // 3. 잔액 확인
        if (member.getProperty() < totalCost) {
            throw new IllegalStateException("돈이 부족합니다! (필요: " + totalCost + "원)");
        }

        // 4. 돈 차감
        member.setProperty(member.getProperty() - totalCost);

        // 5. 내 포트폴리오(Portfolio)에 주식 수량 추가
        Portfolio myPortfolio = portfolioRepository.findByMemberIdAndCompanyId(memberId, companyId)
                .orElse(new Portfolio(member, stockInfo.getCompany(), 0)); // 없으면 0주로 생성

        myPortfolio.setQuantity(myPortfolio.getQuantity() + count);
        portfolioRepository.save(myPortfolio);
    }

    // 📌 [매도] count = "몇 주"를 팔 것인가
    public void sellStock(String memberId, Long companyId, int count) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        // 1. 내 포트폴리오 확인
        Portfolio myPortfolio = portfolioRepository.findByMemberIdAndCompanyId(memberId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("보유한 주식이 없습니다."));

        // 2. 수량 확인
        if (myPortfolio.getQuantity() < count) {
            throw new IllegalStateException("주식이 부족합니다! (보유: " + myPortfolio.getQuantity() + "주)");
        }

        // 3. 주가 테이블(Stock)에서 '현재 가격' 조회
        Stock stockInfo = stockRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("주가 정보 오류"));

        int currentPrice = stockInfo.getCurrentPrice();
        long totalGain = (long) currentPrice * count;

        // 4. 주식 차감 및 돈 증가
        myPortfolio.setQuantity(myPortfolio.getQuantity() - count);
        member.setProperty(member.getProperty() + totalGain);

        // (선택) 수량이 0이면 포트폴리오에서 삭제할 수도 있음
        if (myPortfolio.getQuantity() == 0) {
            portfolioRepository.delete(myPortfolio);
        }
    }
}