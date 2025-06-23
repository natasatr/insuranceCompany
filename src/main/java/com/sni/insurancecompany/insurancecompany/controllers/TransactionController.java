package com.sni.insurancecompany.insurancecompany.controllers;

import com.sni.insurancecompany.insurancecompany.model.Transaction;
import com.sni.insurancecompany.insurancecompany.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public Transaction createTransaction(@RequestBody Transaction t ) {
        return transactionService.save(t);
    }
}
