package com.securebanking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransactionRequest {

    @NotBlank(message = "Transaction type is required")
    private String type;

    @NotBlank(message = "Sender account is required")
    private String senderAccount;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String receiverAccount;
    private String receiverBank;
    private String swiftCode;
    private String digitalSignature;

    public TransactionRequest() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSenderAccount() { return senderAccount; }
    public void setSenderAccount(String senderAccount) { this.senderAccount = senderAccount; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getReceiverAccount() { return receiverAccount; }
    public void setReceiverAccount(String receiverAccount) { this.receiverAccount = receiverAccount; }
    public String getReceiverBank() { return receiverBank; }
    public void setReceiverBank(String receiverBank) { this.receiverBank = receiverBank; }
    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }
    public String getDigitalSignature() { return digitalSignature; }
    public void setDigitalSignature(String digitalSignature) { this.digitalSignature = digitalSignature; }
}
