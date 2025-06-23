package com.sni.insurancecompany.insurancecompany.repositories;


import com.sni.insurancecompany.insurancecompany.model.VerificationEmailCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationEmailCodeRepository extends JpaRepository<VerificationEmailCode, Long> {
    Optional<VerificationEmailCode> findByCodeAndUsernameAndUsedFalseAndExpiredAtAfter(
            String code, String username, LocalDateTime now
    );
}
