package com.securebanking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionResponse {

    private Long id;
    private UUID transactionId;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private String type;
    private String status;
    private String senderAccount;
    private LocalDateTime createdAt;
    private String receiverAccount;
    private String receiverBank;
    private String swiftCode;
    private String digitalSignature;

    public TransactionResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSenderAccount() { return senderAccount; }
    public void setSenderAccount(String senderAccount) { this.senderAccount = senderAccount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getReceiverAccount() { return receiverAccount; }
    public void setReceiverAccount(String receiverAccount) { this.receiverAccount = receiverAccount; }
    public String getReceiverBank() { return receiverBank; }
    public void setReceiverBank(String receiverBank) { this.receiverBank = receiverBank; }
    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }
    public String getDigitalSignature() { return digitalSignature; }
    public void setDigitalSignature(String digitalSignature) { this.digitalSignature = digitalSignature; }
}
