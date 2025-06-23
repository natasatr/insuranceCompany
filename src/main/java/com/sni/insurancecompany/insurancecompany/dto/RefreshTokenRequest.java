package com.sni.insurancecompany.insurancecompany.dto;

import lombok.*;
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {
    private String refreshToken;
}
