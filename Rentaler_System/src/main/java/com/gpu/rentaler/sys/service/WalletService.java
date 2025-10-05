package com.gpu.rentaler.sys.service;

import com.gpu.rentaler.sys.model.Wallet;
import com.gpu.rentaler.sys.repository.WalletRepository;
import com.gpu.rentaler.sys.service.dto.WalletDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {

    @Resource
    private WalletRepository walletRepository;

    public WalletDTO getWalletInfo(Long userId){
        boolean exist = walletRepository.existsWalletByUserId(userId);
        Wallet wallet;
        if(exist){
           wallet = walletRepository.getWalletByUserId(userId);
        }else {
            wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setStatus(0);
            wallet.setBalance(new BigDecimal(0));
            wallet = walletRepository.save(wallet);
        }
        return new WalletDTO(wallet.getId() ,wallet.getUserId() ,wallet.getBalance().toPlainString() ,wallet.getStatus() ,wallet.getLastTransactionTime());
    }

    public void recharge(Long userId ,String amount){
        Wallet wallet = walletRepository.getWalletByUserId(userId);
        BigDecimal balance = wallet.getBalance();
        wallet.setBalance(balance.add(new BigDecimal(amount)));
        walletRepository.save(wallet);
    }
}
