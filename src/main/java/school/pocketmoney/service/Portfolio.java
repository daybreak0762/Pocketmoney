package school.pocketmoney.service;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import school.pocketmoney.domain.Company;
import school.pocketmoney.domain.Member;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 누구의 지갑인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company; // 어떤 회사를 가지고 있는지

    private Integer quantity; // 📌 보유 수량 (예: 5주)

    // (선택 사항) 평균 단가 등
    // private Integer averagePrice; 

    public Portfolio(Member member, Company company, Integer quantity) {
        this.member = member;
        this.company = company;
        this.quantity = quantity;
    }
}