package com.sni.insurancecompany.insurancecompany.controllers;

import com.fasterxml.jackson.annotation.OptBoolean;
import com.sni.insurancecompany.insurancecompany.dto.PolicyDTO;
import com.sni.insurancecompany.insurancecompany.exception.ThreatRequestException;
import com.sni.insurancecompany.insurancecompany.model.Policy;
import com.sni.insurancecompany.insurancecompany.services.PolicyService;
import com.sni.insurancecompany.insurancecompany.services.SIEMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("admin/policies")
@RequiredArgsConstructor
public class PolicyController {
    private final PolicyService policyService;
    private final SIEMService siemService;
    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public List<Policy> getAllPolicies() {
        return policyService.getAllPolicies();
    }
    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> createPolicy(@RequestBody PolicyDTO policyDTO, Authentication authentication) {
        String username = authentication.getName();
        System.out.println(username);
        Policy createdPolicy = policyService.createPolicy(policyDTO, username);
        return ResponseEntity.ok(createdPolicy);

    }
    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyId(@PathVariable Long id) {
        Optional<Policy> policy = policyService.findById(id);
        return policy.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/createdBy/{userId}")
    public List<Policy> getPoliciesByCreatedById(@PathVariable Long userId) {
        return policyService.findByCreatedById(userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> deletePolicy(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            policyService.deletePolicy(id, username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
