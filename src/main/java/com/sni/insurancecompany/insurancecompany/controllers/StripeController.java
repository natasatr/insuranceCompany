package com.sni.insurancecompany.insurancecompany.controllers;

import com.sni.insurancecompany.insurancecompany.model.*;
import com.sni.insurancecompany.insurancecompany.model.enums.UserStatus;
import com.sni.insurancecompany.insurancecompany.repositories.SessionRepository;
import com.sni.insurancecompany.insurancecompany.repositories.UserRepository;
import com.sni.insurancecompany.insurancecompany.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/client/payment")
@RequiredArgsConstructor
public class StripeController {
    private final UserService userService;
    private final PolicyService policyService;
    private final PaymentService paymentService;
    private final SIEMService siemService;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    @PostMapping("/process")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> processPayment(@RequestBody Payment payment){
        try {
            System.out.println("payment process..");
            Optional<User> userOpt = userService.findById(payment.getUserId());
            Optional<Policy> policyOpt = policyService.findById(payment.getPolicyId());

            if (userOpt.isEmpty() || policyOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid user or policy");
            }

            User user = userOpt.get();
            Policy policy = policyOpt.get();


            if (user.getStatus() == UserStatus.BLOCK) {
                siemService.logSecurityEvent("Blocked payment attempt",
                        "Blocked user tried to make payment: " + user.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Your account is blocked. Please contact administrator.");
            }

            if (!payment.getAmount().equals(policy.getPrice())) {
                String message = String.format(
                        "Suspicious payment amount: User %s tried to pay %f instead of policy price %f",
                        user.getUsername(),
                        payment.getAmount(),
                        policy.getPrice()
                );
                siemService.logSecurityEvent("Invalid payment amount", message);

                user.setSuspiciousActivityCount(user.getSuspiciousActivityCount() + 1);
                userRepository.save(user);

                if (user.getSuspiciousActivityCount() >=1) {
                    user.setStatus(UserStatus.BLOCK);
                    userRepository.save(user);
                    List<Session> sessions = sessionRepository.findByUserId(user.getId());
                    sessions.forEach(s -> s.setIsActive(false));
                    sessionRepository.saveAll(sessions);

                    siemService.logSecurityEvent("User blocked",
                            "User blocked due to suspicious payment activities: " + user.getUsername());

                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Your account has been blocked due to suspicious activities.");
                }

                return ResponseEntity.badRequest()
                        .body("Payment amount must match the policy price");
            }

            Purchase purchase = paymentService.processPayment(
                    user,
                    payment.getAmount(),
                    payment.getPolicyName(),
                    payment.getPaymentMethodId(),
                    policy,
                    payment.getCardNumber()
            );

            if (purchase == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Payment processing failed");
            }

            return ResponseEntity.ok(purchase);

        } catch (Exception e) {
            siemService.logSecurityEvent("Payment processing error",
                    "Error processing payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing payment");
        }
    }
}
