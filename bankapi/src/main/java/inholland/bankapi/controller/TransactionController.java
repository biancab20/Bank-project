package inholland.bankapi.controller;

import inholland.bankapi.model.Transaction;
import inholland.bankapi.model.User;
import inholland.bankapi.model.dto.TransactionDTO;
import inholland.bankapi.model.dto.TransactionResponseDTO;
import inholland.bankapi.service.TransactionService;
import inholland.bankapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        TransactionResponseDTO responseDTO = transactionService.createTransaction(transactionDTO);
        return ResponseEntity.ok(responseDTO);
    }
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferFunds(@AuthenticationPrincipal UserDetails userDetails, @RequestBody TransactionDTO transactionDTO) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        Transaction transaction = transactionService.transferFunds(user, transactionDTO);
        return ResponseEntity.ok(transaction);
    }
//working????????

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        List<TransactionResponseDTO> responseDTOs = transactionService.getAllTransactions();
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByUserId(@PathVariable Long userId) {
        List<TransactionResponseDTO> responseDTOs = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(responseDTOs);
    }
}
