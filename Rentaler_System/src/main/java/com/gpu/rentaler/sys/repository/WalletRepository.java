package com.gpu.rentaler.sys.repository;

import com.gpu.rentaler.sys.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet getWalletById(Long id);

    Wallet getWalletByUserId(Long userId);
}
