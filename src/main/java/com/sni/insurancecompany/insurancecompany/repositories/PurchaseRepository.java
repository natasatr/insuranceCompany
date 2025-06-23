package com.sni.insurancecompany.insurancecompany.repositories;

import com.sni.insurancecompany.insurancecompany.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    @Modifying
    @Query("DELETE FROM Purchase p WHERE p.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
