package inholland.bankapi.service;

import inholland.bankapi.model.*;
import inholland.bankapi.model.dto.UserDTO;
import inholland.bankapi.model.dto.UserResponseDTO;
import inholland.bankapi.repository.AccountRepository;
import inholland.bankapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accountRepository = accountRepository;
    }
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapUserToUserResponseDTO)
                .collect(Collectors.toList());
    }
    public User addUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Email address already in use.");
        }
        if (userRepository.findByBsn(userDTO.bsn()).isPresent()) {
            throw new IllegalArgumentException("BSN already in use.");
        }
        User user = mapObjectToUser(userDTO);
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.password()));
        user.setRole(UserRole.CUSTOMER); // Setting default role
        user.setUserAccountStatus(UserAccountStatus.PENDING); // Setting default account status
        return userRepository.save(user);
    }

    private User mapObjectToUser(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setBsn(userDTO.bsn());
        user.setEmail(userDTO.email());
        user.setPhoneNumber(userDTO.phoneNumber());
        return user;
    }

    private UserResponseDTO mapUserToUserResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getBsn(), user.getPhoneNumber(), user.getEmail(), user.getRole(), user.getUserAccountStatus());
    }
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }

    public User updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        existingUser.setFirstName(userDTO.firstName());
        existingUser.setLastName(userDTO.lastName());
        existingUser.setBsn(userDTO.bsn());
        existingUser.setEmail(userDTO.email());
        existingUser.setPhoneNumber(userDTO.phoneNumber());
        existingUser.setRole(userDTO.role());
        existingUser.setUserAccountStatus(userDTO.userAccountStatus());
        // Only update password if it's provided in the DTO
        if (userDTO.password() != null && !userDTO.password().isEmpty()) {
            existingUser.setPassword(bCryptPasswordEncoder.encode(userDTO.password()));
        }

        return userRepository.save(existingUser);
    }
    public User approveCustomer(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (user.getUserAccountStatus() != UserAccountStatus.PENDING) {
            throw new IllegalArgumentException("Only pending accounts can be approved.");
        }

        user.setUserAccountStatus(UserAccountStatus.ACTIVE);
        user = userRepository.save(user);

        // Create checking and savings accounts
        createAccountForUser(user, AccountType.CHECKING);
        createAccountForUser(user, AccountType.SAVINGS);

        return user;
    }
    private void createAccountForUser(User user, AccountType accountType) {
        Account account = new Account();
        account.setOwner(user);
        account.setType(accountType);
        account.setBalance(0.0);
        account.setDailyLimit(1000.0); // Example limit
        account.setAbsoluteLimit(0.0);
        account.setIban(IbanService.generateIban());
        account.setStatus(AccountStatus.ACTIVE);

        accountRepository.save(account);
    }
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        user.setUserAccountStatus(UserAccountStatus.CLOSED);
        userRepository.save(user);

        // Deactivate all associated accounts
        List<Account> userAccounts = accountRepository.findByOwner(user);
        for (Account account : userAccounts) {
            account.setStatus(AccountStatus.INACTIVE);
            accountRepository.save(account);
        }
    }
}
