package com.sni.insurancecompany.insurancecompany.repositories;

import com.sni.insurancecompany.insurancecompany.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Optional<Policy> findById(Long id);
    List<Policy> findByCreatedById(Long createdById);
}
