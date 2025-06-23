package com.sni.insurancecompany.insurancecompany.services;

import com.sni.insurancecompany.insurancecompany.model.Transaction;
import com.sni.insurancecompany.insurancecompany.repositories.TransactionReposistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionReposistory transactionReposistory;
    public Transaction save(Transaction t) {
        return transactionReposistory.save(t);
    }
}
