package inholland.bankapi.service;

import inholland.bankapi.model.*;
import inholland.bankapi.model.dto.TransactionDTO;
import inholland.bankapi.model.dto.TransactionResponseDTO;
import inholland.bankapi.repository.AccountRepository;
import inholland.bankapi.repository.TransactionRepository;
import inholland.bankapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public TransactionResponseDTO createTransaction(TransactionDTO transactionDTO) {
        Account sender = findAccountById(transactionDTO.senderAccountId());
        Account receiver = findAccountById(transactionDTO.receiverAccountId());
        User userPerforming = findUserById(transactionDTO.userPerformingId());

        validateTransaction(sender, receiver, userPerforming, transactionDTO.amount(), userPerforming.getRole());

        performTransaction(sender, receiver, transactionDTO.amount());

        Transaction transaction = saveTransaction(sender, receiver, userPerforming, transactionDTO);

        return mapTransactionToTransactionResponseDTO(transaction);
    }
    private Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found: " + accountId));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
    }

    private void validateTransaction(Account sender, Account receiver, User userPerforming, double amount, UserRole userRole) {
        if (userRole != UserRole.EMPLOYEE) {
            if (sender.getOwner().getId().equals(receiver.getOwner().getId())) {
                // Internal transfer (same user)
                if (!sender.getOwner().getId().equals(userPerforming.getId())) {
                    throw new IllegalArgumentException("User performing the transaction does not own the accounts.");
                }
            } else {
                // External transfer (different users)
                if (sender.getType() != AccountType.CHECKING || receiver.getType() != AccountType.CHECKING) {
                    throw new IllegalArgumentException("Transfers to other users can only be done from checking account to checking account.");
                }
            }
        } else {
            // Employee performing transfer
            if (sender.getType() != AccountType.CHECKING || receiver.getType() != AccountType.CHECKING) {
                throw new IllegalArgumentException("Transfers can only be done from checking account to checking account.");
            }
        }

        if (sender.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds in the sender account");
        }

        if (sender.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds in the sender account");
        }

        if ((sender.getBalance() - amount) < sender.getAbsoluteLimit()) {
            throw new IllegalArgumentException("Transaction would exceed the absolute limit of the sender account");
        }

        // Here you can add additional checks for daily/transaction limits if needed
        double dailyTotal = transactionRepository.findBySenderAndTimeBetween(
                        sender, LocalDateTime.now().minusDays(1), LocalDateTime.now())
                .stream().mapToDouble(Transaction::getAmount).sum();

        if (dailyTotal + amount > sender.getDailyLimit()) {
            throw new IllegalArgumentException("Transaction amount exceeds daily limit");
        }
    }

    private void performTransaction(Account sender, Account receiver, double amount) {
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        accountRepository.save(sender);
        accountRepository.save(receiver);
    }

    private Transaction saveTransaction(Account sender, Account receiver, User userPerforming, TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setUserPerforming(userPerforming);
        transaction.setAmount(transactionDTO.amount());
        transaction.setComment(transactionDTO.comment());
        transaction.setTime(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    private TransactionResponseDTO mapTransactionToTransactionResponseDTO(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getSender().getAccountId(),
                transaction.getReceiver().getAccountId(),
                transaction.getUserPerforming().getId(),
                transaction.getTime(),
                transaction.getAmount(),
                transaction.getComment()
        );
    }

    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapTransactionToTransactionResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> getTransactionHistoryByUserId(Long userId) {
        User user = findUserById(userId);
        List<Account> userAccounts = accountRepository.findByOwner(user);
        Set<Transaction> transactions = new HashSet<>();

        // Add transactions performed by the user
        transactions.addAll(transactionRepository.findByUserPerforming(user));

        // Add transactions involving the user's accounts
        userAccounts.forEach(account -> {
            transactions.addAll(transactionRepository.findBySenderOrReceiver(account, account));
        });

        return transactions.stream()
                .map(this::mapTransactionToTransactionResponseDTO)
                .collect(Collectors.toList());
    }
}
