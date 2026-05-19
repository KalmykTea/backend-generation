package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.ATMRequestDTO;
import com.example.generation.dtos.RequestDTOs.TransferRequestDTO;
import com.example.generation.dtos.ResponseDTOs.ATMResponseDTO;
import com.example.generation.dtos.ResponseDTOs.TransferResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.entities.User;
import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import com.example.generation.enums.Role;
import com.example.generation.enums.TransactionType;
import com.example.generation.mappers.ResponseDTOMappers.ATMResponseDTOMapper;
import com.example.generation.mappers.ResponseDTOMappers.TransferResponseDTOMapper;
import com.example.generation.repositories.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final TransferResponseDTOMapper transferResponseDTOMapper;
    private final ATMResponseDTOMapper atmResponseDTOMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountService accountService,
                              TransferResponseDTOMapper transferResponseDTOMapper, ATMResponseDTOMapper atmResponseDTOMapper) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.transferResponseDTOMapper = transferResponseDTOMapper;
        this.atmResponseDTOMapper = atmResponseDTOMapper;
    }

    // basic stuff, input custom logic according to your user stories
    public Iterable<Transaction> findAll(){
        return transactionRepository.findAll();
    }

    public Optional<Transaction> findById(Long id){
        return transactionRepository.findById(id);
    }

    public Transaction save(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    public Page<TransferResponseDTO> getTransactionsByAccountIBAN(String accountIBAN, Pageable pageable) {
        accountService.getAccountByIbanOrThrow(accountIBAN);

        return transactionRepository
                .findByAccountIBAN(accountIBAN, pageable)
                .map(transferResponseDTOMapper::toDTO);
    }

    @Transactional
    public TransferResponseDTO processTransfer(TransferRequestDTO dto) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (dto.getTransactionType().equals(TransactionType.TRANSFER)) {
            Account fromAccount = accountService.getAccountByIbanOrThrow(dto.getFromAccountIban());
            validateAccountForTransaction(fromAccount, "Sender account");

            if (currentUser.getRole() == Role.CUSTOMER && !fromAccount.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You can only transfer from your own account");
            }

            Account toAccount = accountService.getAccountByIbanOrThrow(dto.getToAccountIban());
            validateAccountForTransaction(toAccount, "Receiver account");

            validateTransferAccounts(fromAccount, toAccount);

            fromAccount.transact(dto.getAmount(), TransactionType.TRANSFER);
            toAccount.transact(dto.getAmount(), TransactionType.DEPOSIT);
            accountService.save(fromAccount);
            accountService.save(toAccount);
            Transaction saved = transactionRepository.save(buildTransaction(fromAccount, toAccount, dto, currentUser));
            return transferResponseDTOMapper.toDTO(saved);
        }
        else throw new IllegalArgumentException("Transaction type must be transfer");
    }

    @Transactional
    public ATMResponseDTO processATMRequest(ATMRequestDTO dto) {
        Account account = accountService.getAccountByIbanOrThrow(dto.getIban());
        validateAccountForTransaction(account, "Sender account");
        switch (dto.getTransactionType()) {
            case WITHDRAWAL:
                account.transact(dto.getAmount(), TransactionType.WITHDRAWAL);
                break;
            case DEPOSIT:
                account.transact(dto.getAmount(), TransactionType.DEPOSIT);
                break;
            default:
                throw new IllegalArgumentException("Unsupported ATM transaction type: " + dto.getTransactionType());
        }

        accountService.save(account);
        Transaction transaction = buildTransaction(account, dto);
        transactionRepository.save(transaction);
        return atmResponseDTOMapper.toDTO(transaction);
    }

    private void validateAccountForTransaction(Account account, String accountName) {
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException(accountName + " is not active");
        }
    }

    private void validateTransferAccounts(Account fromAccount, Account toAccount) {
        boolean differentUsers = !fromAccount.getUser().getId().equals(toAccount.getUser().getId());
        boolean notBothChecking = fromAccount.getAccountType() != AccountType.CHECKING ||
                toAccount.getAccountType() != AccountType.CHECKING;

        if (differentUsers && notBothChecking) {
            throw new IllegalArgumentException("Both accounts need to be of type CHECKING");
        }
    }

    private Transaction buildTransaction(Account fromAccount, Account toAccount, TransferRequestDTO dto, User initiatedBy) {
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setInitiatedBy(initiatedBy);

        return transaction;
    }

    //use functional programming later to merge the two build transactions... not sure how to do that yet
    private Transaction buildTransaction(Account account, ATMRequestDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setFromAccount(account);
        transaction.setToAccount(account);
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setInitiatedBy(account.getUser());
        return transaction;
    }

    public Page<Transaction> findTransactionsByUserId(Long userId, Pageable pageable) {
        return transactionRepository.findTransactionsByUserId(userId, pageable);
    }
}
