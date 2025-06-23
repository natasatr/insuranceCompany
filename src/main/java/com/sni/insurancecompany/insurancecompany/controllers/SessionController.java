package com.sni.insurancecompany.insurancecompany.controllers;

import com.sni.insurancecompany.insurancecompany.model.Session;
import com.sni.insurancecompany.insurancecompany.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/sessions")
@PreAuthorize("hasAuthority('EMPLOYEE')")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @GetMapping
    public List<Session> getAllSessions() {
        return sessionService.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Session> getSessionByUserId(@PathVariable Long userId) {
        return  sessionService.findByUserId(userId);
    }

    @PostMapping
    public Session createSession(@RequestBody Session session) {
        return sessionService.save(session);
    }

    @PutMapping("/{id}/deactivate")
    public Session deactivateSession(@PathVariable Long id) {
        return sessionService.deactivate(id);
    }
}
