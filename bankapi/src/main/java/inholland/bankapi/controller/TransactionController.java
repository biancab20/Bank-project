package inholland.bankapi.controller;

import inholland.bankapi.model.Transaction;
import inholland.bankapi.model.User;
import inholland.bankapi.model.dto.TransactionDTO;
import inholland.bankapi.model.dto.TransactionResponseDTO;
import inholland.bankapi.service.TransactionService;
import inholland.bankapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        try {
            TransactionResponseDTO createdTransaction = transactionService.createTransaction(transactionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        List<TransactionResponseDTO> responseDTOs = transactionService.getAllTransactions();
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistoryByUserId(@PathVariable Long userId) {
        List<TransactionResponseDTO> responseDTOs = transactionService.getTransactionHistoryByUserId(userId);
        return ResponseEntity.ok(responseDTOs);
    }
}
