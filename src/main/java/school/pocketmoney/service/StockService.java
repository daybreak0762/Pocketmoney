package school.pocketmoney.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.pocketmoney.domain.Company;
import school.pocketmoney.domain.Stock;
import school.pocketmoney.dto.StockRequestDto;
import school.pocketmoney.repository.CompanyRepository;
import school.pocketmoney.repository.StockRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final CompanyRepository companyRepository;
    private final StockRepository stockRepository;

    /**
     * 주가를 등록하고, 등록된 날짜의 다음 분기 시작 날짜를 반환합니다.
     */
    @Transactional
    public LocalDate registerQuarterlyStock(StockRequestDto dto) {
        // 1. Company 엔티티 조회
        Company company = companyRepository.findById(dto.getCoNum())
                .orElseThrow(() -> new IllegalArgumentException("기업 번호(coNum)를 찾을 수 없습니다: " + dto.getCoNum()));

        // 2. 중복 날짜 데이터 체크
        Optional<Stock> existingStock = stockRepository.findByCompanyAndStockDate(company, dto.getStockDate());
        if (existingStock.isPresent()) {
            throw new IllegalStateException(
                    company.getCoName() + "의 " + dto.getStockDate() + " 주가 데이터가 이미 존재합니다."
            );
        }

        // 3. Stock 엔티티 생성 및 저장
        Stock stock = Stock.builder()
                .company(company)
                .stock(dto.getStock())
                .stockDate(dto.getStockDate()) // 📌 날짜 저장
                .build();

        stockRepository.save(stock);

        // 4. 📌 다음 분기 시작 날짜를 계산하여 Controller로 반환
        return calculateNextQuarterStartDate(dto.getStockDate());
    }

    // 특정 회사의 모든 주가 데이터 조회
    public List<Stock> getHistoricalQuarterlyStocks(Long coNum) {
        Company company = companyRepository.findById(coNum)
                .orElseThrow(() -> new IllegalArgumentException("기업 번호(coNum)를 찾을 수 없습니다: " + coNum));

        return stockRepository.findByCompanyOrderByStockDateDesc(company);
    }

    /**
     * 주어진 날짜의 다음 분기 시작 날짜를 계산합니다.
     * (3개월 후의 1일)
     */
    public LocalDate calculateNextQuarterStartDate(LocalDate currentDate) {
        // 현재 날짜에서 3개월을 더한 후, 그 달의 1일로 설정합니다.
        // 예: 2015-01-01 -> 2015-04-01
        // 예: 2023-12-01 -> 2024-03-01
        return currentDate.plusMonths(3).withDayOfMonth(1);
    }

    /**
     * 특정 회사의 가장 최근 주가 날짜를 조회하여 다음 등록 날짜를 계산합니다.
     * 초기 등록 시에는 '2015-01-01'을 기본값으로 사용할 수 있습니다.
     */
    @Transactional(readOnly = true)
    public LocalDate getNextRegistrationDate(Long coNum) {
        // 1. 회사 엔티티를 찾습니다.
        Company company = companyRepository.findById(coNum)
                .orElseThrow(() -> new IllegalArgumentException("기업 번호(coNum)를 찾을 수 없습니다: " + coNum));

        // 2. 가장 최근 등록된 주가 데이터를 조회합니다.
        List<Stock> recentStocks = stockRepository.findByCompanyOrderByStockDateDesc(company);

        if (recentStocks.isEmpty()) {
            // 주가 데이터가 없는 경우, 초기 시작 날짜를 반환합니다.
            return LocalDate.of(2015, 1, 1);
        } else {
            // 가장 최근 날짜의 다음 분기 시작 날짜를 계산하여 반환합니다.
            LocalDate lastDate = recentStocks.get(0).getStockDate();
            return calculateNextQuarterStartDate(lastDate);
        }
    }
}