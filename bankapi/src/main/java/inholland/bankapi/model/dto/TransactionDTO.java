package inholland.bankapi.model.dto;

import inholland.bankapi.model.Account;
import inholland.bankapi.model.AccountType;
import inholland.bankapi.model.User;

import java.time.LocalDateTime;

public record TransactionDTO(Long senderAccountId, Long receiverAccountId, Long userPerformingId, LocalDateTime time, double amount, String comment) {
}