package school.pocketmoney.controller;

// CompanyController.java 또는 관련 Controller 파일

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.pocketmoney.domain.Company;
import school.pocketmoney.service.CompanyService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// CompanyController.java 또는 관련 Controller 파일

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    // 📌 1. CompanyService 변수를 선언합니다.
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // 📌 이 메서드의 내부를 임시로 가장 단순하게 수정하여 500 오류가 사라지는지 테스트
    @GetMapping("/list")
    public ResponseEntity<List<Company>> getCompanyList() {
        // 📌 원래의 Service/DB 호출 로직을 사용합니다.
        return ResponseEntity.ok(companyService.findAllCompanies());
    }

    // 📌 특정 회사의 주가 기록 요청 API
    @GetMapping("/{id}/history")
    public ResponseEntity<List<Integer>> getCompanyHistory(@PathVariable Long id) {
        // 테스트용 더미 데이터 생성 (실제로는 DB의 StockHistory 테이블에서 가져와야 함)
        List<Integer> history = new ArrayList<>();

        // 예: 최근 10턴의 주가를 랜덤으로 생성
        int currentPrice = 10000; // 시작가
        for (int i = 0; i < 10; i++) {
            history.add(currentPrice);
            // -500원 ~ +500원 변동
            currentPrice += (int) (Math.random() * 1000) - 500;
        }

        System.out.println("그래프 데이터 요청 받음 (회사 ID: " + id + ")");
        return ResponseEntity.ok(history);
    }
}