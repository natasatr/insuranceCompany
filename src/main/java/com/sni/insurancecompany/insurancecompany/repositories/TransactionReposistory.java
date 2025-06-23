package com.sni.insurancecompany.insurancecompany.repositories;

import com.sni.insurancecompany.insurancecompany.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionReposistory extends JpaRepository<Transaction, Long> {
}
