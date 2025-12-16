// school.pocketmoney.domain.Stock.java (수정)
package school.pocketmoney.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Stock")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recordId")
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coNum")
    private Company company;

    // 📌 분기 시작일로 사용될 날짜 필드 추가
    @Column(nullable = false)
    private LocalDate stockDate;

    // 📌 주가
    private Long stock;

    @Builder
    public Stock(Company company, LocalDate stockDate, Long stock) {
        this.company = company;
        this.stockDate = stockDate;
        this.stock = stock;
    }
}