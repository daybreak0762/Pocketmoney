package school.pocketmoney.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Portfolios") // DB 테이블명과 일치
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Portfolio {

    // 📌 기본 키 (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동 생성되는 PK (BIGINT 타입)
    @Column(name = "portfoliosNum")
    private Long portfoliosNum;

    // 📌 외래 키 (Foreign Key) - Member (거래주체)
    // [파란 열쇠: Member의 userId를 받아옴]
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "userId") // Portfolios 테이블의 FK 컬럼명
    private Member member;

    // 📌 외래 키 (Foreign Key) - Company (거래 기업)
    // [파란 열쇠: Company의 coNum을 받아옴]
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "coNum") // Portfolios 테이블의 FK 컬럼명
    private Company company;

    // 📌 거래 방식 (0 = 매수, 1 = 매도)
    @Column(name = "type")
    private Integer tradeType;

    // 📌 수량
    private Integer amount;

    // 📌 단가
    private Integer price;

    // @Builder를 위한 전체 필드 생성자 (Lombok에서 처리)
    @Builder
    public Portfolio(Long portfoliosNum, Member member, Company company, Integer tradeType, Integer amount, Integer price) {
        this.portfoliosNum = portfoliosNum;
        this.member = member;
        this.company = company;
        this.tradeType = tradeType;
        this.amount = amount;
        this.price = price;
    }
}
