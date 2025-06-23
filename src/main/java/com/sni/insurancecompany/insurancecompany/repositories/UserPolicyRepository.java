package com.sni.insurancecompany.insurancecompany.repositories;

import com.sni.insurancecompany.insurancecompany.model.User;
import com.sni.insurancecompany.insurancecompany.model.UserPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserPolicyRepository extends JpaRepository<UserPolicy, Long> {
    Optional<UserPolicy> findByUserIdAndPolicyId(Long userId, Long policyId);
    List<UserPolicy> findByUserId(Long userId);
    @Modifying
    @Query("DELETE FROM UserPolicy up WHERE up.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
