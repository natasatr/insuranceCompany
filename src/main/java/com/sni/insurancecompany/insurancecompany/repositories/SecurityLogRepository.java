package com.sni.insurancecompany.insurancecompany.repositories;

import com.sni.insurancecompany.insurancecompany.model.SecurityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityLogRepository extends JpaRepository<SecurityLog, Long> {


}
