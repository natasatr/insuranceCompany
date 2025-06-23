package com.sni.insurancecompany.insurancecompany.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SecurityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String eventType;
    @Column(nullable = false)
    private String description;
}
