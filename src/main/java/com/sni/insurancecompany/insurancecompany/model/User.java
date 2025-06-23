package com.sni.insurancecompany.insurancecompany.model;

import com.sni.insurancecompany.insurancecompany.model.enums.Role;
import com.sni.insurancecompany.insurancecompany.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    private int failed2FAAttempts=0;
    private int failedLoginAttempts = 0;
    private int suspiciousActivityCount = 0;

}
