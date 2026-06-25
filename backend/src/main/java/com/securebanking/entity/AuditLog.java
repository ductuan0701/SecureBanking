package com.securebanking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String username;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "ip_address")
    private String ipAddress;

    private String browser;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog() {}

    public AuditLog(String action, String username, Long transactionId, String ipAddress, String browser) {
        this.action = action;
        this.username = username;
        this.transactionId = transactionId;
        this.ipAddress = ipAddress;
        this.browser = browser;
    }

    public Long getId() { return id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
