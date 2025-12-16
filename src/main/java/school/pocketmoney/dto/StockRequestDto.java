package school.pocketmoney.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StockRequestDto {
    private Long coNum;         // 기업 번호
    private LocalDate stockDate; // 📌 주가 날짜 (분기 시작일)
    private Long stock;         // 주가
}