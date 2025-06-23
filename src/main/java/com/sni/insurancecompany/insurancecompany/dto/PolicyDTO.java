package com.sni.insurancecompany.insurancecompany.dto;

import lombok.Data;

@Data
public class PolicyDTO {
    private String policyname;
    private String type;
    private String description;
    private Double price;
    private Integer userId;
}
