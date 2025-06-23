package com.sni.insurancecompany.insurancecompany.services;

import com.sni.insurancecompany.insurancecompany.dto.RegisterUserRequest;
import com.sni.insurancecompany.insurancecompany.model.enums.Role;
import com.sni.insurancecompany.insurancecompany.model.User;
import com.sni.insurancecompany.insurancecompany.model.enums.UserStatus;
import com.sni.insurancecompany.insurancecompany.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.channels.FileChannel;
import java.nio.file.AccessDeniedException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPolicyRepository userPolicyRepository;
    private final SessionRepository sessionRepository;
    private final PurchaseRepository purchaseRepository;

    public User registerEmployee(RegisterUserRequest registerEmployeeRequest) {
        System.out.println("provjera1");
        validateRequest(registerEmployeeRequest);
        System.out.println("provjera");
        User zaposleni = new User();
        zaposleni.setFirstName(registerEmployeeRequest.getFirstName());
        zaposleni.setLastName(registerEmployeeRequest.getLastName());
        zaposleni.setUsername(registerEmployeeRequest.getUsername());
        zaposleni.setPassword(passwordEncoder.encode(registerEmployeeRequest.getPassword()));
        zaposleni.setEmail(registerEmployeeRequest.getEmail() !=null ? registerEmployeeRequest.getEmail() : " ");
        zaposleni.setRole(Role.EMPLOYEE);
        return userRepository.save(zaposleni);
    }

    public int getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Korisnik nije pronadjeen: " + username));
        return user.getId().intValue();
    }

    public void registerClient(RegisterUserRequest request) {
            validateRequest(request);
            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setRole(Role.CLIENT);
            userRepository.save(user);
         //   String code = generateVerificationCode();
         //   VerificationEmailCode verificationEmailCode = new VerificationEmailCode(code, request.getUsername(), LocalDateTime.now().plusMinutes(10));
          //  verificationEmailCodeRepository.save(verificationEmailCode);
          //  emailService.sendVerificationCode(request.getEmail(), code);
    }

    public void validateRequest(RegisterUserRequest request) {
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        if(request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be minimun 6 chars");
        }
    }public String generateVerificationCode() {
        SecureRandom rand = new SecureRandom();
        int code = 100000+rand.nextInt(900000);
        return String.valueOf(code);
    }
    public List<User> getActiveClients() {
        return userRepository.findByRole(Role.CLIENT);
    }
    public Optional<User> getClientById(Long id) {
        return userRepository.findById(id);
    }
    public User updateClient(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstName(updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName());
                    user.setEmail(updatedUser.getEmail());
                    user.setUsername(updatedUser.getUsername());
                    user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }
    @Transactional
    public void deleteClient(Long id) throws AccessDeniedException {
        User client = userRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Client not found"));
        if(!client.getRole().equals(Role.CLIENT)){
            throw new AccessDeniedException("Only clients can be deleted");
        }
        purchaseRepository.deleteByUserId(id);
        sessionRepository.deleteByUserId(id);
        userPolicyRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }
    public void blockClient(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(UserStatus.BLOCK);
            userRepository.save(user);
        });
    }
    public void unBlockClient(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        });

    }

    public Optional<User> getClientByUsername(String username) {
           return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
}
