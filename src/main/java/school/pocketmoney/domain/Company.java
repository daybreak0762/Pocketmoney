package school.pocketmoney.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Company") // DB 테이블명과 일치
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company {

    // 📌 기본 키 (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "co_num")
    private Long coNum;

    // 📌 기업명
    @Column(name = "co_name", length = 255)
    private String coName;

    // 📌 분야
    @Column(name = "field", length = 255)
    private String field;

    // 📌 연관 관계: Stock (일대다)
    // Company가 Stock에 대해 'coNum'을 통해 FK로 연결되어 있으므로 연관 관계 정의
    @OneToMany(mappedBy = "company")
    private List<Stock> stocks;

    // 📌 연관 관계: Portfolio (일대다)
    @OneToMany(mappedBy = "company")
    private List<Portfolio> portfolios;

    // 📌 연관 관계: Hint (일대다)
    @OneToMany(mappedBy = "company")
    private List<Hint> hints;

    @Builder
    public Company(Long coNum, String coName, String field) {
        this.coNum = coNum;
        this.coName = coName;
        this.field = field;
    }
}
