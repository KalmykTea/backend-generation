package com.example.generation.entities;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import jakarta.persistence.*;
import lombok.*;

import com.example.generation.enums.TransactionStatus;
import com.example.generation.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
@FilterDef(name = "dateRangeFilter", parameters = {
    @ParamDef(name = "startDate", type = java.time.LocalDateTime.class),
    @ParamDef(name = "endDate", type = java.time.LocalDateTime.class)
})
@FilterDef(name = "amountLtFilter", parameters = @ParamDef(name = "amountLt", type = java.math.BigDecimal.class))
@FilterDef(name = "amountGtFilter", parameters = @ParamDef(name = "amountGt", type = java.math.BigDecimal.class))
@FilterDef(name = "amountEqFilter", parameters = @ParamDef(name = "amountEq", type = java.math.BigDecimal.class))
@FilterDef(name = "ibanFilter", parameters = @ParamDef(name = "iban", type = String.class))
@Filter(name = "dateRangeFilter", condition = "timestamp >= :startDate AND timestamp <= :endDate")
@Filter(name = "amountLtFilter", condition = "amount < :amountLt")
@Filter(name = "amountGtFilter", condition = "amount > :amountGt")
@Filter(name = "amountEqFilter", condition = "amount = :amountEq")
@Filter(name = "ibanFilter", condition = "(EXISTS (SELECT 1 FROM account a WHERE a.id = from_account AND a.iban LIKE :iban) OR EXISTS (SELECT 1 FROM account a WHERE a.id = to_account_id AND a.iban LIKE :iban))")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_account_iban", nullable = true)
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_iban", nullable = true)
    private Account toAccount;

    @ManyToOne
    @JoinColumn(name = "initiated_by", nullable = false)
    private User initiatedBy;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    private String description;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

}