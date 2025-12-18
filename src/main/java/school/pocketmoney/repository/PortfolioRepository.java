package school.pocketmoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.pocketmoney.domain.Portfolio;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    // 내 아이디와 회사 아이디로 보유 주식 찾기
    Optional<Portfolio> findByMemberIdAndCompanyId(String memberId, Long companyId);
}
