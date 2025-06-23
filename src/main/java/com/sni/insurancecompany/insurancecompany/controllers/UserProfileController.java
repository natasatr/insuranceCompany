package com.sni.insurancecompany.insurancecompany.controllers;

import com.sni.insurancecompany.insurancecompany.model.User;
import com.sni.insurancecompany.insurancecompany.services.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/profiles")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserService userService;
    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<User> getClientById(@PathVariable String username) {
        System.out.println("Dohvati user-a sa id"+username);
        return userService.getClientByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
