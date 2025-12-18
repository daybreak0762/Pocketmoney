package school.pocketmoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.pocketmoney.domain.Stock;
import school.pocketmoney.domain.Company;
import java.time.LocalDate; // 📌 LocalDate import
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    // 📌 특정 회사의 특정 날짜 주가 데이터가 이미 존재하는지 확인
    Optional<Stock> findByCompanyAndStockDate(Company company, LocalDate stockDate);

    // 📌 특정 회사의 모든 주가 데이터를 날짜 내림차순으로 조회 (최신 날짜 순)
    List<Stock> findByCompanyOrderByStockDateDesc(Company company);

    Optional<Stock> findByCompanyId(Long companyId);
}