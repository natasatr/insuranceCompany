package com.sni.insurancecompany.insurancecompany.controllers;

import com.sni.insurancecompany.insurancecompany.model.User;
import com.sni.insurancecompany.insurancecompany.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/admin/dashboard/client")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<List<User>> getActiveClients() {
        return ResponseEntity.ok(userService.getActiveClients());
    }
    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<User> getClientByUsername(@PathVariable String username) {
        System.out.println("Dohvati user-a sa id"+username);
        return userService.getClientByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<User> updateClient(@PathVariable Long id, @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.updateClient(id, updatedUser));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) throws AccessDeniedException {
        System.out.println("Pokusaj brisanja");
        userService.deleteClient(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/block")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<Void> blockClient(@PathVariable Long id) {
        userService.blockClient(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/unblock")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<Void> unblockClient(@PathVariable Long id) {
        userService.unBlockClient(id);
        return ResponseEntity.ok().build();
    }
}
