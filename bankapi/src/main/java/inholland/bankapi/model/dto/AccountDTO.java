package inholland.bankapi.model.dto;

import inholland.bankapi.model.AccountStatus;
import inholland.bankapi.model.AccountType;
import inholland.bankapi.model.User;

public record AccountDTO(Long accountId, double balance, double dailyLimit, double absoluteLimit, String iban, AccountStatus status, AccountType type, Long ownerId) {}

