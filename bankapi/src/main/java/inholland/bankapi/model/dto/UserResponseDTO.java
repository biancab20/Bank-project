package inholland.bankapi.model.dto;

import inholland.bankapi.model.UserAccountStatus;
import inholland.bankapi.model.UserRole;

public record UserResponseDTO(Long id, String firstName, String lastName, String bsn, String phoneNumber, String email, UserRole role, UserAccountStatus userAccountStatus) {}

