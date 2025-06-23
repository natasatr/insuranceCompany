package com.sni.insurancecompany.insurancecompany.repositories;

import com.sni.insurancecompany.insurancecompany.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.context.request.SessionScope;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findBySessionToken(String token);
    List<Session> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM Session s where s.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
