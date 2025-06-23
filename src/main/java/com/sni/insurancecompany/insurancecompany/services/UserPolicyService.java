package com.sni.insurancecompany.insurancecompany.services;

import com.sni.insurancecompany.insurancecompany.model.UserPolicy;
import com.sni.insurancecompany.insurancecompany.repositories.UserPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPolicyService {
    private final UserPolicyRepository userPolicyRepository;

    public List<UserPolicy> getPoliciesByUserId(Long userId) {
        return userPolicyRepository.findByUserId(userId);
    }
    public void deletePolicy(Long userId, Long policyId) {
        userPolicyRepository.findByUserIdAndPolicyId(userId, policyId)
                .ifPresentOrElse(
                        userPolicy -> userPolicyRepository.delete(userPolicy),
                        () -> { throw new IllegalArgumentException("Not found"); }
                );
    }
}
