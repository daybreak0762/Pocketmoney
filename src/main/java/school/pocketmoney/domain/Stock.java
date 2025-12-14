package school.pocketmoney.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Stock") // DB 테이블명과 일치
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    // 📌 기본 키 (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동 생성되는 PK (BIGINT 타입)
    @Column(name = "recordId")
    private Long recordId;

    // 📌 외래 키 (Foreign Key) - Company (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coNum") // Stock 테이블의 FK 컬럼명
    private Company company;

    // 📌 주가
    private Long stock;

    // 📌 분기 (quarter)
    private String quarter;

    // 📌 직전 분기 (작전분기)
    @Column(name = "직전분기")
    private String lastQuarter;

    @Builder
    public Stock(Long recordId, Company company, Long stock, String quarter, String lastQuarter) {
        this.recordId = recordId;
        this.company = company;
        this.stock = stock;
        this.quarter = quarter;
        this.lastQuarter = lastQuarter;
    }
}