package school.pocketmoney.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HintRequestDto {
    private Long coNum;
    private String level;
    private String htDate; // 클라이언트로부터 String으로 입력받음
    private String text;
    private Integer point;
}
