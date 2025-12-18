package school.pocketmoney.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.pocketmoney.dto.TradeRequest;

import school.pocketmoney.service.TradeService;

import java.util.Map;

// TradeController.java
@RestController
@RequestMapping("/api")
public class TradeController {

    @Autowired
    private TradeService tradeService; // 서비스 연결

    @PostMapping("/trade")
    public ResponseEntity<String> trade(@RequestBody TradeRequest request) {
        System.out.println("거래 요청 받음: " + request.getTradeType() + ", 금액: " + request.getAmount());

        try {
            // 실제 거래 로직 실행 (DB 업데이트)
            tradeService.processTrade(request);
            return ResponseEntity.ok("Trade Successful");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Trade Failed: " + e.getMessage());
        }
    }

    @PostMapping("/buy")
    public ResponseEntity<String> buyStock(@RequestBody Map<String, Object> request) {
        try {
            String memberId = (String) request.get("memberId");
            Long companyId = Long.valueOf(String.valueOf(request.get("companyId")));

            // 📌 여기 들어오는 숫자는 이제 '개수(주)' 입니다.
            int count = Integer.parseInt(String.valueOf(request.get("amount")));

            tradeService.buyStock(memberId, companyId, count);
            return ResponseEntity.ok("매수 성공: " + count + "주");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}