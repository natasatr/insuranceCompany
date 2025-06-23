package com.sni.insurancecompany.insurancecompany.services;

import com.sni.insurancecompany.insurancecompany.dto.PolicyDTO;
import com.sni.insurancecompany.insurancecompany.model.Policy;
import com.sni.insurancecompany.insurancecompany.model.User;
import com.sni.insurancecompany.insurancecompany.repositories.PolicyRepository;
import com.sni.insurancecompany.insurancecompany.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    //private final ClientRepository clientRepository;
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }
    public Optional<Policy> findById(Long id){
        return policyRepository.findById(id);
    }
    public Policy save(Policy policy) {
        return policyRepository.save(policy);
    }
    public Policy createPolicy(PolicyDTO policyDTO, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Nije pronadjen"));
        Policy policy = new Policy();
        policy.setPolicyname(policyDTO.getPolicyname());
        policy.setType(policyDTO.getType());
        policy.setDescription(policyDTO.getDescription());
        policy.setPrice(policyDTO.getPrice());
        policy.setCreatedBy(user);
        return policyRepository.save(policy);
    }
    public List<Policy> findByCreatedById(Long userId) {
        return policyRepository.findByCreatedById(userId);
    }

    public void deletePolicy(Long id, String username) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Policy not found with id: " + id));

        policyRepository.deleteById(id);
    }
}
