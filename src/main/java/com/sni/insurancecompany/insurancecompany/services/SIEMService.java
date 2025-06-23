package com.sni.insurancecompany.insurancecompany.services;

import com.sni.insurancecompany.insurancecompany.model.SecurityLog;
import com.sni.insurancecompany.insurancecompany.repositories.SecurityLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
@Service
public class SIEMService {
    private static final Logger logger = Logger.getLogger(SIEMService.class.getName());
    @Autowired
    private SecurityLogRepository securityLogRepository;
    public void logSecurityEvent(String type, String decription) {
        String logMsg = String.format("Time: %s, Event: %s, Description: %s", LocalDateTime.now(), type, decription);
        logger.log(Level.WARNING, logMsg);

        SecurityLog securityLog = new SecurityLog();
        securityLog.setEventType(type);
        securityLog.setDescription(decription);
        securityLogRepository.save(securityLog);
    }
}