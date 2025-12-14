package school.pocketmoney.service;

import school.pocketmoney.domain.Company;
import school.pocketmoney.domain.Member;
import school.pocketmoney.dto.CompanyRequestDto;
import school.pocketmoney.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final MemberService memberService; // 관리자 권한 확인을 위한 서비스

    /**
     * 관리자 권한을 확인하고 새로운 기업을 등록합니다.
     * @param adminId 기업 등록을 요청한 관리자의 ID
     * @param dto 등록할 기업 정보
     * @return 저장된 Company 엔티티
     */
    public Company registerCompany(String adminId, CompanyRequestDto dto) {
        // 1. 관리자 권한 확인
        // MemberService를 통해 관리자 Member 엔티티를 조회
        Member adminMember = memberService.getMemberById(adminId);

        // adminMember가 존재하지 않거나, 관리자(ad 필드)가 false인 경우 예외 발생
        // (isAdmin() 메서드가 Member 엔티티에 있다고 가정합니다. 없으면 getAd()로 확인)
        if (adminMember == null || !adminMember.isAdmin()) {
            throw new IllegalStateException("관리자 권한이 없습니다. 기업을 등록할 수 없습니다.");
        }

        // 2. Company Entity 생성 (coNum은 @GeneratedValue로 자동 할당)
        Company newCompany = Company.builder()
                .coName(dto.getCoName())
                .field(dto.getField())
                .build();

        // 3. DB 저장 및 반환
        return companyRepository.save(newCompany);
    }

    // school.pocketmoney.service.CompanyService.java (추가)

    // ...
    @Transactional(readOnly = true)
    public List<Company> findAllCompanies() {
        return companyRepository.findAll();
    }
}