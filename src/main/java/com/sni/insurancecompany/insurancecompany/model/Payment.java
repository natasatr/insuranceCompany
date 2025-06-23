package com.sni.insurancecompany.insurancecompany.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private Long userId;
    private Long policyId;
    private Double amount;
    private String policyName;
    private String paymentMethodId;
    private String cardNumber;
}
