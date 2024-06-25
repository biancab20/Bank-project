package inholland.bankapi.controller;

import inholland.bankapi.model.User;
import inholland.bankapi.model.UserAccountStatus;
import inholland.bankapi.model.dto.LoginDTO;
import inholland.bankapi.model.dto.UserDTO;
import inholland.bankapi.model.dto.UserResponseDTO;
import inholland.bankapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            userDTO = new UserDTO(
                    userDTO.firstName(),
                    userDTO.lastName(),
                    userDTO.bsn(),
                    userDTO.phoneNumber(),
                    userDTO.email(),
                    bCryptPasswordEncoder.encode(userDTO.password()), // Encode the password
                    userDTO.role(),
                    userDTO.userAccountStatus()
            );
            User newUser = userService.addUser(userDTO);
            UserResponseDTO responseDTO = new UserResponseDTO(
                    newUser.getId(),
                    newUser.getFirstName(),
                    newUser.getLastName(),
                    newUser.getBsn(),
                    newUser.getPhoneNumber(),
                    newUser.getEmail(),
                    newUser.getRole(),
                    newUser.getUserAccountStatus()
            );
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.getUserByEmail(loginDTO.email());
            if (user != null && bCryptPasswordEncoder.matches(loginDTO.password(), user.getPassword())) {
                if (user.getUserAccountStatus() == UserAccountStatus.PENDING) {
                    return ResponseEntity.ok("Welcome! Your account is pending approval.");
                }
                // Implement your token generation logic here and return it.
                // For simplicity, we are returning a success message.
                return ResponseEntity.ok("Login successful!");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

}
