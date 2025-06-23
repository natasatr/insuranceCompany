package com.sni.insurancecompany.insurancecompany.controllers;

import com.sni.insurancecompany.insurancecompany.model.Policy;
import com.sni.insurancecompany.insurancecompany.model.UserPolicy;
import com.sni.insurancecompany.insurancecompany.services.PolicyService;
import com.sni.insurancecompany.insurancecompany.services.UserPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("client/user-policies")
@RequiredArgsConstructor
public class UserPolicyController {
    private final UserPolicyService userPolicyService;
    private final PolicyService policyService;
    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public List<Policy> getAllPolicies() {
        return policyService.getAllPolicies();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public List<UserPolicy> getPoliciesByUserId(@PathVariable Long userId) {
        return userPolicyService.getPoliciesByUserId(userId);
    }

    @DeleteMapping("/{userId}/policy/{policyId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public void deletePolicy(@PathVariable Long userId, @PathVariable Long policyId) {
         userPolicyService.deletePolicy(userId, policyId);
    }
}
