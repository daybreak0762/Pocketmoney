package school.pocketmoney.controller;

// CompanyController.java 또는 관련 Controller 파일

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
