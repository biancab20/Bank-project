package inholland.bankapi.controller;

import inholland.bankapi.model.User;
import inholland.bankapi.model.UserAccountStatus;
import inholland.bankapi.model.dto.UserDTO;
import inholland.bankapi.model.dto.UserResponseDTO;
import inholland.bankapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserResponseDTO responseDTO = new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBsn(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getRole(),
                user.getUserAccountStatus()
        );
        return ResponseEntity.ok(responseDTO);
    }

    //maybe this is not needed
    @GetMapping("/email")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@RequestParam String email) {
        User user = userService.getUserByEmail(email);
        UserResponseDTO responseDTO = new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBsn(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getRole(),
                user.getUserAccountStatus()
        );
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User user = userService.updateUser(id, userDTO);
        UserResponseDTO responseDTO = new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBsn(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getRole(),
                user.getUserAccountStatus()
        );
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> addUser(@RequestBody UserDTO userDTO) {
        User user = userService.addUser(userDTO);
        UserResponseDTO responseDTO = new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBsn(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getRole(),
                user.getUserAccountStatus()
        );
        return ResponseEntity.ok(responseDTO);
    }
    @PutMapping("/approve/{id}")
    public ResponseEntity<UserResponseDTO> approveCustomer(@PathVariable Long id) {
        User user = userService.approveCustomer(id);
        UserResponseDTO responseDTO = new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBsn(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getRole(),
                user.getUserAccountStatus()
        );
        return ResponseEntity.ok(responseDTO);
    }
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<UserResponseDTO> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        User user = userService.getUserById(id);
        UserResponseDTO responseDTO = new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBsn(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getRole(),
                user.getUserAccountStatus()
        );
        return ResponseEntity.ok(responseDTO);
    }
}
