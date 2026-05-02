package pl.matthes0.gym.member.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberCreateDto(
        @NotBlank(message = "Full name is required")
        @Size(max=100, message = "Full name is too long")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Incorrect email format")
        @Size(max=100, message = "Email is too long")
        String email) {
}
