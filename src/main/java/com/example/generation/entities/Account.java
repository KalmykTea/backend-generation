package com.example.generation.entities;

import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import com.example.generation.enums.TransactionType;
import com.example.generation.framework.exceptions.DailyLimitReachedException;
import com.example.generation.framework.exceptions.InsufficientBalanceException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(unique = true, nullable = false)
    private String iban;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "absolute_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal absoluteLimit = BigDecimal.ZERO;

    @Column(name = "daily_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal dailyLimit = new BigDecimal("1000.00");

    @Column(name = "daily_transfer", nullable = false, precision = 15, scale = 2)
    private BigDecimal dailyTransfer = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "last_transfer_date", nullable = false)
    private LocalDateTime lastTransferDate = LocalDateTime.now();

    public void transact(BigDecimal amount, TransactionType type){
        if (this.accountStatus == AccountStatus.CLOSED) {
            throw new IllegalStateException("Cannot perform transactions on a closed account");
        }
        BigDecimal newBalance;
        LocalDateTime today = LocalDateTime.now();
        BigDecimal currentTransferTally = dailyTransfer.add(amount);

        if (!today.equals(lastTransferDate)) {
            dailyTransfer = BigDecimal.ZERO;
            lastTransferDate = today;
        }

        if (type == TransactionType.DEPOSIT) {
                newBalance = balance.add(amount);
        }
        else if(currentTransferTally.compareTo(dailyLimit) <= 0){
            dailyTransfer = currentTransferTally;
            newBalance = balance.subtract(amount);
        }
        else throw new DailyLimitReachedException();

        if (newBalance.compareTo(absoluteLimit) < 0) {
            throw new InsufficientBalanceException();
        }
        balance = newBalance;
    }

}