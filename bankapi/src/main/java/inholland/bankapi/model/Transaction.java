package inholland.bankapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_account", nullable = false)
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "receiver_account", nullable = false)
    private Account receiver;

    @ManyToOne
    @JoinColumn(name = "user_performing", nullable = false)
    private User userPerforming;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private double amount;

    private String comment;

}
