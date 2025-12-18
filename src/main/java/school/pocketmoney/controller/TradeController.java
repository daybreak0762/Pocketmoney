package school.pocketmoney.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.pocketmoney.dto.TradeRequest;
import school.pocketmoney.service.TradeService;

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
}