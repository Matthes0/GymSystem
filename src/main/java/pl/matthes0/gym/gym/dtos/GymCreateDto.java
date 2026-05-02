package pl.matthes0.gym.gym.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GymCreateDto(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name is too long")
        String name,

        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address is too long")
        String address,

        @NotBlank(message = "Phone number is required")
        @Size(max = 20, message = "Phone number is too long")
        String phoneNumber) {
}
