package com.sni.insurancecompany.insurancecompany.security;

import com.sni.insurancecompany.insurancecompany.model.User;
import com.sni.insurancecompany.insurancecompany.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User nije pronadjen"+username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
               Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
