package pl.matthes0.gym.gym.dtos;

import jakarta.validation.constraints.NotBlank;

public record GymCreateDto(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Address is required")
        String address,
        @NotBlank(message = "Phone number is required")
        String phoneNumber) {
}
