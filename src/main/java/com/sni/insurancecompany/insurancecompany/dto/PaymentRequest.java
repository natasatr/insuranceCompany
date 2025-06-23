package com.sni.insurancecompany.insurancecompany.dto;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private Long userId;
    private Long policyId;
    private Double amount;
    private String policyName;
    private String paymentMethodId;
    private String cardNumber;
}
