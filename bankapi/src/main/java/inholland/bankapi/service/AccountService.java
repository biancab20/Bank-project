package inholland.bankapi.service;

import inholland.bankapi.model.Account;
import inholland.bankapi.model.User;
import inholland.bankapi.model.dto.AccountDTO;
import inholland.bankapi.repository.AccountRepository;
import inholland.bankapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final IbanService ibanService;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository, IbanService ibanService) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.ibanService = ibanService;
    }
    public Account createAccount(AccountDTO accountDTO) {
        Account account = mapObjectToAccount(accountDTO);
        account.setIban(IbanService.generateIban());
        return accountRepository.save(account);
    }
    private Account mapObjectToAccount(AccountDTO accountDTO) {
        Account account = new Account();
        account.setAbsoluteLimit(accountDTO.absoluteLimit());
        account.setDailyLimit(accountDTO.dailyLimit());
        account.setBalance(accountDTO.balance());
        account.setType(accountDTO.type());

        User owner = userRepository.findById(accountDTO.ownerId())
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
        account.setOwner(owner);

        return account;
    }
    public List<Account> getAccountsByUser(User user) {
        return accountRepository.findByOwner(user);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByOwner_Id(userId);
    }

    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }

}
