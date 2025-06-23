package com.sni.insurancecompany.insurancecompany.services;

import com.sni.insurancecompany.insurancecompany.model.Purchase;
import com.sni.insurancecompany.insurancecompany.repositories.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    public final PurchaseRepository purchaseRepository;
    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }
    public Purchase save(Purchase p) {
        return purchaseRepository.save(p);
    }

}
