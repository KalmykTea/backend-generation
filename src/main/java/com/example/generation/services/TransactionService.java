package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.AccountTransactionRequestDTO;
import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.entities.User;
import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import com.example.generation.enums.Role;
import com.example.generation.enums.TransactionType;
import com.example.generation.mappers.ResponseDTOMappers.TransactionResponseDTOMapper;
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
    private final TransactionResponseDTOMapper transactionResponseDTOMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountService accountService,
                              TransactionResponseDTOMapper transactionResponseDTOMapper) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.transactionResponseDTOMapper = transactionResponseDTOMapper;
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

    public Page<TransactionResponseDTO> getTransactionsByAccountIBAN(String accountIBAN, Pageable pageable) {
        accountService.getAccountByIbanOrThrow(accountIBAN);

        return transactionRepository
                .findByAccountIBAN(accountIBAN, pageable)
                .map(transactionResponseDTOMapper::toDTO);
    }

    @Transactional
    public TransactionResponseDTO processTransfer(TransactionRequestDTO dto) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (dto.getTransactionType().equals(TransactionType.TRANSFER)) {
            Account fromAccount = accountService.getAccountByIbanOrThrow(dto.getFromAccount().getIban());
            validateAccountForTransaction(fromAccount, "Sender account");

            if (currentUser.getRole() == Role.CUSTOMER && !fromAccount.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You can only transfer from your own account");
            }

            Account toAccount = accountService.getAccountByIbanOrThrow(dto.getToAccount().getIban());
            validateAccountForTransaction(toAccount, "Receiver account");

            validateTransferAccounts(fromAccount, toAccount);

            fromAccount.transact(dto.getAmount(), TransactionType.TRANSFER);
            toAccount.transact(dto.getAmount(), TransactionType.DEPOSIT);
            accountService.save(fromAccount);
            accountService.save(toAccount);
            Transaction saved = transactionRepository.save(buildTransaction(fromAccount, toAccount, dto, currentUser));
            return transactionResponseDTOMapper.toDTO(saved);
        }
        else throw new IllegalArgumentException("Transaction type must be transfer");
    }

    @Transactional
    public TransactionResponseDTO processATMRequest(TransactionRequestDTO dto) {
        AccountTransactionRequestDTO accountDTO = switch (dto.getTransactionType()) {
            case WITHDRAWAL -> dto.getFromAccount();
            case DEPOSIT    -> dto.getToAccount();
            default -> throw new IllegalArgumentException(
                    "Unsupported ATM transaction type: " + dto.getTransactionType()
            );
        };

        Account account = accountService.getAccountByIbanOrThrow(accountDTO.getIban());
        validateAccountForTransaction(account, "Account");
        account.transact(dto.getAmount(), dto.getTransactionType());

        Transaction transaction = buildTransaction(account, dto);
        transactionRepository.save(transaction);
        accountService.save(account);

        return transactionResponseDTOMapper.toDTO(transaction);
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

    private Transaction buildTransaction(Account fromAccount, Account toAccount, TransactionRequestDTO dto, User initiatedBy) {
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
    private Transaction buildTransaction(Account account, TransactionRequestDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setFromAccount(account);
        transaction.setToAccount(account);
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setInitiatedBy(account.getUser()); //change this
        return transaction;
    }

    public Page<Transaction> findTransactionsByUserId(Long userId, Pageable pageable) {
        return transactionRepository.findTransactionsByUserId(userId, pageable);
    }

    //Maybe have one policy for transaction stuff (put shared methods into one policy)
}
