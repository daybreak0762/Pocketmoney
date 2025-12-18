// TradeRequest.java
package school.pocketmoney.dto; // 패키지명은 본인 프로젝트에 맞게

public class TradeRequest {
    private String tradeType; // "BUY" 또는 "SELL"
    private Long companyId;   // coNum (회사 ID)
    private Integer amount;   // 거래 금액

    // Getters and Setters
    public String getTradeType() { return tradeType; }
    public void setTradeType(String tradeType) { this.tradeType = tradeType; }
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
}