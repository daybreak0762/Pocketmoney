package school.pocketmoney.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.pocketmoney.domain.Hint;
import school.pocketmoney.domain.Member;
import school.pocketmoney.dto.HintRequestDto;
import school.pocketmoney.repository.HintRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class HintService {

    private final HintRepository hintRepository;
    private final MemberService memberService; // 관리자 확인용

    // 날짜 변환 포맷 정의 (예: YYYY-MM-DD)
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Transactional
    public Hint addHint(String adminId, HintRequestDto dto) {

        // 1. 관리자 권한 확인
        Member adminMember = memberService.getMemberById(adminId);
        if (!adminMember.isAdmin()) {
            throw new IllegalStateException("관리자 권한이 없습니다. 힌트를 추가할 수 없습니다.");
        }

        try {
            // 2. String Date를 java.util.Date로 변환
            Date htDate = dateFormat.parse(dto.getHtDate());

            // 3. Hint Entity 생성
            Hint newHint = Hint.builder()
                    .coNum(dto.getCoNum())
                    .level(dto.getLevel())
                    .htDate(htDate)
                    .text(dto.getText())
                    .point(dto.getPoint())
                    .build();

            // 4. DB 저장
            return hintRepository.save(newHint);

        } catch (ParseException e) {
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다. (YYYY-MM-DD 형식 필요)");
        }
    }
}
