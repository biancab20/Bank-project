package inholland.bankapi.model.dto;

import inholland.bankapi.model.UserAccountStatus;
import inholland.bankapi.model.UserRole;

public record UserDTO(String firstName, String lastName, String bsn, String phoneNumber, String email, String password, UserRole role, UserAccountStatus userAccountStatus) {
}
