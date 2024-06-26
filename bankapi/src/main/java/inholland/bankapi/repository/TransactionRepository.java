package inholland.bankapi.repository;

import inholland.bankapi.model.Account;
import inholland.bankapi.model.Transaction;
import inholland.bankapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserPerforming(User user);
    List<Transaction> findBySenderOrReceiver(Account sender, Account receiver);
    List<Transaction> findBySenderAndTimeBetween(Account sender, LocalDateTime start, LocalDateTime end);


}
