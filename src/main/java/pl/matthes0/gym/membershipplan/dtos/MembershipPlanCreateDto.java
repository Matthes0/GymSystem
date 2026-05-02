package pl.matthes0.gym.membershipplan.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import pl.matthes0.gym.membershipplan.Plan;
import pl.matthes0.gym.membershipplan.Price;

public record MembershipPlanCreateDto(
        @NotBlank(message = "Name is required")
        @Size(max=100, message = "Name is too long")
        String name,

        @NotNull(message = "Plan is required")
        Plan plan,

        @NotNull(message = "Price details are required")
        @Valid
        Price monthlyPrice,

        @NotNull(message = "Duration is required")
        @Positive(message = "Duration in months must be greater than zero")
        Integer durationInMonths,

        @NotNull(message = "Max members are required")
        @Positive(message = "Max members must be greater than zero")
        Integer maxMembers) {
}
