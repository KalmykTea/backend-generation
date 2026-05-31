package com.example.generation.domain.policy;

import com.example.generation.dtos.RequestDTOs.ATMRequestDTO;
import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.User;
import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import com.example.generation.enums.TransactionType;
import com.example.generation.framework.exceptions.DailyLimitReachedException;
import com.example.generation.framework.exceptions.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionPolicyTest {
    private TransactionPolicy transactionPolicy;
    private ATMRequestDTO atmRequestDTO1;
    private ATMRequestDTO atmRequestDTO2;
    private TransactionRequestDTO transactionRequestDTO;
    private TransactionRequestDTO transactionRequestDTO2;
    private Account checkingAccount1;
    private Account checkingAccount2;
    private Account savingsAccount;
    private Account inactiveAccount;
    private final BigDecimal currentWithdrawalTotal = BigDecimal.valueOf(10);

    @BeforeEach
    void setUp(){
        transactionPolicy = new TransactionPolicy();
        atmRequestDTO1 = new ATMRequestDTO();
        atmRequestDTO2 = new ATMRequestDTO();
        transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO2 = new TransactionRequestDTO();
        User customer1 = new User();
        User customer2 = new User();
        checkingAccount1 = new Account();
        checkingAccount2 = new Account();
        savingsAccount = new Account();
        inactiveAccount = new Account();

        customer1.setId(1L);
        customer2.setId(2L);

        atmRequestDTO1.setTransactionType(TransactionType.DEPOSIT);
        atmRequestDTO2.setTransactionType(TransactionType.TRANSFER);
        transactionRequestDTO.setTransactionType(TransactionType.TRANSFER);
        transactionRequestDTO.setAmount(BigDecimal.valueOf(20));
        transactionRequestDTO2.setTransactionType(TransactionType.WITHDRAWAL);
        transactionRequestDTO2.setAmount(BigDecimal.valueOf(200));

        checkingAccount1.setUser(customer1);
        checkingAccount1.setAccountType(AccountType.CHECKING);
        checkingAccount1.setAccountStatus(AccountStatus.ACTIVE);
        checkingAccount1.setBalance(BigDecimal.ZERO);
        checkingAccount1.setDailyLimit(BigDecimal.valueOf(100));
        checkingAccount1.setAbsoluteLimit(BigDecimal.valueOf(-100));

        checkingAccount2.setUser(customer2);
        checkingAccount2.setAccountType(AccountType.CHECKING);
        checkingAccount2.setAccountStatus(AccountStatus.ACTIVE);

        savingsAccount.setUser(customer2);
        savingsAccount.setAccountType(AccountType.SAVINGS);
        savingsAccount.setAccountStatus(AccountStatus.ACTIVE);

        inactiveAccount.setUser(customer1);
        inactiveAccount.setAccountStatus(AccountStatus.CLOSED);
    }

    @Test
    void enforceAccountMustBeActive_throwsForInActiveAccount(){
        assertThrows(IllegalArgumentException.class,()->
                transactionPolicy.enforceAccountMustBeActive(inactiveAccount, "inactive"));
    }

    @Test
    void enforceAccountMustBeActive_allowsActiveAccount(){
        assertDoesNotThrow(()->
                transactionPolicy.enforceAccountMustBeActive(checkingAccount1, "CheckingAccount1"));
    }

    @Test
    void enforceValidATMTransactionType_throwsForInvalidTransactionType(){
        assertThrows(IllegalArgumentException.class,
                ()-> transactionPolicy.enforceValidATMTransactionType(atmRequestDTO2));
    }

    @Test
    void enforceValidATMRequestType_allowsValidTransactionType() {
        assertDoesNotThrow(() -> transactionPolicy.enforceValidATMTransactionType(atmRequestDTO1));
    }

    @Test
    void enforceTransactionMustBeTypeTransfer_throwsForInvalidTransactionType(){
        assertThrows(IllegalArgumentException.class,
                ()-> transactionPolicy.enforceTransactionMustBeTypeTransfer(transactionRequestDTO2.getTransactionType()));
    }

    @Test
    void enforceTransactionMustBeTypeTransfer_allowsValidTransactionType() {
        assertDoesNotThrow(() ->
                transactionPolicy.enforceTransactionMustBeTypeTransfer(transactionRequestDTO.getTransactionType()));
    }

    @Test
    void enforceAccountsMustBelongToDifferentUsers_throwsForSameUsers(){
        assertThrows(IllegalArgumentException.class,()->
                transactionPolicy.enforceAccountsMustBelongToDifferentUsers(checkingAccount1, checkingAccount1));
    }

    @Test
    void enforceAccountsMustBelongToDifferentUsers_allowsDifferentUsers(){
        assertDoesNotThrow(() ->
                transactionPolicy.enforceAccountsMustBelongToDifferentUsers(checkingAccount1, checkingAccount2));
    }

    @Test
    void enforceAccountsMustBeTypeChecking_throwsForAccountsThatAreNotChecking(){
        assertThrows(IllegalArgumentException.class,()->
                transactionPolicy.enforceAccountsMustBeTypeChecking(checkingAccount1, savingsAccount));
    }

    @Test
    void enforceAccountsMustBeTypeChecking_allowsCheckingAccounts(){
        assertDoesNotThrow(()->
                transactionPolicy.enforceAccountsMustBeTypeChecking(checkingAccount1, checkingAccount2));
    }

    @Test
    void enforceValidTransfer_throwsForInvalidTransfer(){
        assertThrows(IllegalArgumentException.class,()->
                transactionPolicy.enforceValidTransfer(checkingAccount1, savingsAccount,
                        transactionRequestDTO.getTransactionType()));
    }

    @Test
    void enforceValidTransfer_allowsValidTransfer(){
        assertDoesNotThrow(()->
                transactionPolicy.enforceValidTransfer(checkingAccount1, checkingAccount2,
                        transactionRequestDTO.getTransactionType()));
    }

    @Test
    void enforceValidATMTransaction_throwsForInvalidATMTransaction(){
        assertThrows(IllegalArgumentException.class,()->
                transactionPolicy.enforceValidATMTransaction(atmRequestDTO2, checkingAccount1));
    }

    @Test
    void enforceValidATMTransaction_allowsValidATMTransaction(){
        assertDoesNotThrow(()->
                transactionPolicy.enforceValidATMTransaction(atmRequestDTO1, checkingAccount1));
    }

    @Test
    void enforceDailyLimit_throwsForExceededDailyLimit(){
        assertThrows(DailyLimitReachedException.class,()->
                transactionPolicy.enforceDailyLimit(transactionRequestDTO2.getTransactionType(), checkingAccount1.getDailyLimit(),
                        currentWithdrawalTotal.add(transactionRequestDTO2.getAmount())));
    }

    @Test
    void enforceDailyLimit_allowsUnexceededDailyLimit(){
        assertDoesNotThrow(()->
                transactionPolicy.enforceDailyLimit(transactionRequestDTO.getTransactionType(), checkingAccount1.getDailyLimit(),
                        currentWithdrawalTotal.add(transactionRequestDTO.getAmount())));
    }

    @Test
    void enforceDailyLimit_throwsForExceededAbsoluteLimit(){
        assertThrows(InsufficientBalanceException.class,()->
                transactionPolicy.enforceAbsoluteLimit(checkingAccount1.getAbsoluteLimit(),
                        checkingAccount1.getBalance().add(transactionRequestDTO2.getAmount().negate())));
    }//0 + (-200) exceeds absolute limit of -100

    @Test
    void enforceAbsoluteLimit_allowsUnexceededAbsoluteLimit(){
        assertDoesNotThrow(()->
                transactionPolicy.enforceAbsoluteLimit(checkingAccount1.getAbsoluteLimit(),
                        checkingAccount1.getBalance().add(transactionRequestDTO.getAmount().negate())));
    }//0 + (-20) does not exceed limit of -100
}
