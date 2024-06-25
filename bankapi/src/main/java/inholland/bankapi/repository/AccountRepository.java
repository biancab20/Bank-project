package inholland.bankapi.repository;

import inholland.bankapi.model.Account;
import inholland.bankapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwner(User owner);
    List<Account> findByOwner_Id(Long userId);
}
