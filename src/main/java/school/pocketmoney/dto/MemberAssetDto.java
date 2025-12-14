package school.pocketmoney.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberAssetDto {
    private String memberId;
    private Long property; // 자산
    private Integer pt;    // 포인트
}