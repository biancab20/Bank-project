package inholland.bankapi.service;

import inholland.bankapi.model.Account;
import inholland.bankapi.model.Transaction;
import inholland.bankapi.model.User;
import inholland.bankapi.model.dto.TransactionDTO;
import inholland.bankapi.model.dto.TransactionResponseDTO;
import inholland.bankapi.repository.AccountRepository;
import inholland.bankapi.repository.TransactionRepository;
import inholland.bankapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.time.LocalDateTime;
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
        Account sender = accountRepository.findById(transactionDTO.senderAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Sender account not found."));
        Account receiver = accountRepository.findById(transactionDTO.receiverAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver account not found."));
        User userPerforming = userRepository.findById(transactionDTO.userPerformingId())
                .orElseThrow(() -> new EntityNotFoundException("User performing transaction not found."));

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setUserPerforming(userPerforming);
        transaction.setAmount(transactionDTO.amount());
        transaction.setComment(transactionDTO.comment());
        transaction.setTime(LocalDateTime.now());

        transaction = transactionRepository.save(transaction);

        return new TransactionResponseDTO(transaction.getId(), sender.getAccountId(), receiver.getAccountId(), userPerforming.getId(), transaction.getTime(), transaction.getAmount(), transaction.getComment());
    }

    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapTransactionToTransactionResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> getTransactionsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
        return transactionRepository.findByUserPerforming(user).stream()
                .map(this::mapTransactionToTransactionResponseDTO)
                .collect(Collectors.toList());
    }

    private TransactionResponseDTO mapTransactionToTransactionResponseDTO(Transaction transaction) {
        return new TransactionResponseDTO(transaction.getId(), transaction.getSender().getAccountId(), transaction.getReceiver().getAccountId(), transaction.getUserPerforming().getId(), transaction.getTime(), transaction.getAmount(), transaction.getComment());
    }
    @Transactional
    public Transaction transferFunds(User user, TransactionDTO transactionDTO) {
        Account fromAccount = accountRepository.findById(transactionDTO.senderAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Sender account not found"));
        Account toAccount = accountRepository.findById(transactionDTO.receiverAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver account not found"));

        if (!fromAccount.getOwner().getId().equals(user.getId()) || !toAccount.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Accounts must belong to the logged-in user");
        }

        if (fromAccount.getBalance() < transactionDTO.amount()) {
            throw new IllegalArgumentException("Insufficient funds in the from account");
        }

        fromAccount.setBalance(fromAccount.getBalance() - transactionDTO.amount());
        toAccount.setBalance(toAccount.getBalance() + transactionDTO.amount());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setSender(fromAccount);
        transaction.setReceiver(toAccount);
        transaction.setUserPerforming(user);
        transaction.setAmount(transactionDTO.amount());
        transaction.setComment(transactionDTO.comment());
        transaction.setTime(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
}
