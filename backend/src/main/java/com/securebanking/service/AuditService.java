package com.securebanking.service;

import com.securebanking.entity.AuditLog;
import com.securebanking.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void log(String action, String username, Long transactionId, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String browser = request.getHeader("User-Agent");

        AuditLog auditLog = new AuditLog(action, username, transactionId, ipAddress,
                browser != null ? browser : "Unknown");
        auditLogRepository.save(auditLog);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
