package inholland.bankapi.service;

import inholland.bankapi.model.User;
import inholland.bankapi.model.dto.UserDTO;
import inholland.bankapi.model.dto.UserResponseDTO;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
        User user = mapObjectToUser(userDTO);
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.password()));
        return userRepository.save(user);
    }
    private User mapObjectToUser(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setBsn(userDTO.bsn());
        user.setEmail(userDTO.email());
        user.setPhoneNumber(userDTO.phoneNumber());
        user.setRole(userDTO.role());
        user.setUserAccountStatus(userDTO.userAccountStatus());
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

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found.");
        }
        userRepository.deleteById(id);
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
}
