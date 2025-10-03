package com.gpu.rentaler.sys.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet", schema = "gpu_rentaler_0")
public class Wallet extends BaseEntity {

    private Long userId;

    private BigDecimal balance;

    private Integer status;

    private Instant updatedAt;

    private Instant lastTransactionTime;

    private String remark;

    @PreUpdate
    @PrePersist
    protected void onCreate() {
        this.updatedAt = Instant.now();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getStatus() {
        return status;
    }


    public void setStatus(Integer status) {
        this.status = status;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getLastTransactionTime() {
        return lastTransactionTime;
    }

    public void setLastTransactionTime(Instant lastTransactionTime) {
        this.lastTransactionTime = lastTransactionTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
