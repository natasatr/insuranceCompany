package com.sni.insurancecompany.insurancecompany.services;

import com.sni.insurancecompany.insurancecompany.model.Session;
import com.sni.insurancecompany.insurancecompany.repositories.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    public List<Session> findAll() {
        return sessionRepository.findAll();
    }

    public List<Session> findByUserId(Long userId) {
        return sessionRepository.findByUserId(userId);
    }

    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    public Session deactivate(Long id) {
        Optional<Session> sessionOptional = sessionRepository.findById(id);
        if(sessionOptional.isPresent()) {
            Session session = sessionOptional.get();
            session.setIsActive(false);
            return sessionRepository.save(session);
        }
        else {
            throw new IllegalArgumentException("Sesion not found with ID" +id);
        }
    }
}
