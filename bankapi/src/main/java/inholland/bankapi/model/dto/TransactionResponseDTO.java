package inholland.bankapi.model.dto;

import java.time.LocalDateTime;

public record TransactionResponseDTO(Long id, Long senderAccountId, Long receiverAccountId, Long userPerformingId, LocalDateTime time, double amount, String comment) {
}
