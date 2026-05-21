package com.example.generation.domain.policy;

import com.example.generation.dtos.RequestDTOs.ATMRequestDTO;
import com.example.generation.entities.Account;
import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import com.example.generation.enums.TransactionType;
import org.springframework.stereotype.Component;

@Component
public class TransactionPolicy {

    public void enforceValidTransfer(Account fromAccount, Account toAccount, TransactionType transactionType){
        enforceTransactionMustBeTypeTransfer(transactionType);
        enforceAccountMustBeActive(fromAccount, "fromAccount");
        enforceAccountMustBeActive(toAccount, "toAccount");
        enforceAccountsMustBelongToDifferentUsers(fromAccount, toAccount);
        enforceAccountsMustBeTypeChecking(fromAccount, toAccount);
    }

    public void enforceValidATMTransaction(ATMRequestDTO dto, Account account) {
        enforceAccountMustBeActive(account, "Account");
        enforceValidATMTransactionType(dto);
    }

    public void enforceAccountMustBeActive(Account account, String accountName) {
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException(accountName + " is not active");
        }
    }

    public void enforceValidATMTransactionType(ATMRequestDTO dto) {
        if(dto.getTransactionType() != TransactionType.DEPOSIT &&
                dto.getTransactionType() != TransactionType.WITHDRAWAL){
            throw new IllegalArgumentException("Transaction type must be DEPOSIT or WITHDRAWAL");
        }
    }

    public void enforceTransactionMustBeTypeTransfer(TransactionType transactionType) {
        if(transactionType != TransactionType.TRANSFER){
            throw new IllegalArgumentException("Transaction type must be TRANSFER");
        }
    }

    public void enforceAccountsMustBelongToDifferentUsers(Account fromAccount, Account toAccount) {
        if(fromAccount.getUser().getId().equals(toAccount.getUser().getId())){
            throw new IllegalArgumentException("Accounts must belong to different users");
        }
    }

    public void enforceAccountsMustBeTypeChecking(Account fromAccount, Account toAccount) {
        if(fromAccount.getAccountType() != AccountType.CHECKING ||
                toAccount.getAccountType() != AccountType.CHECKING){
            throw new IllegalArgumentException("Both accounts need to be of type CHECKING");
        }
    }

}
