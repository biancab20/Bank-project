package inholland.bankapi.controller;

import inholland.bankapi.model.Account;
import inholland.bankapi.model.dto.AccountDTO;
import inholland.bankapi.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        Account account = accountService.createAccount(accountDTO);
        AccountDTO responseDTO = new AccountDTO(
                account.getAccountId(),
                account.getBalance(),
                account.getDailyLimit(),
                account.getAbsoluteLimit(),
                account.getIban(),
                account.getStatus(),
                account.getType(),
                account.getOwner().getId()
        );
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByUser(@PathVariable Long userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        List<AccountDTO> responseDTOs = accounts.stream().map(account -> new AccountDTO(
                account.getAccountId(),
                account.getBalance(),
                account.getDailyLimit(),
                account.getAbsoluteLimit(),
                account.getIban(),
                account.getStatus(),
                account.getType(),
                account.getOwner().getId()
        )).toList();
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        List<AccountDTO> responseDTOs = accounts.stream().map(account -> new AccountDTO(
                account.getAccountId(),
                account.getBalance(),
                account.getDailyLimit(),
                account.getAbsoluteLimit(),
                account.getIban(),
                account.getStatus(),
                account.getType(),
                account.getOwner().getId()
        )).toList();
        return ResponseEntity.ok(responseDTOs);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
