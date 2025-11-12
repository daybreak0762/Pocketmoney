package school.pocketmoney.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "Hint")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "htNum", nullable = false)
    private Long htNum; // 힌트번호

    @Column(name = "coNum", nullable = false)
    private Long coNum; // 기업번호

    @Column(name = "Level")
    private String level; // 힌트 등급

    @Column(name = "htDate") // 힌트 적용일 (DATE)
    @Temporal(TemporalType.DATE)
    private Date htDate;

    @Column(name = "Text", columnDefinition = "VARCHAR(255)") // VARCHAR(255)로 반영
    private String text; // 힌트내용

    @Column(name = "Point")
    private Integer point; // 소모포인트

    @Builder
    public Hint(Long coNum, String level, Date htDate, String text, Integer point) {
        this.coNum = coNum;
        this.level = level;
        this.htDate = htDate;
        this.text = text;
        this.point = point;
    }
}