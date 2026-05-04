package pl.matthes0.gym.member.dtos;

import pl.matthes0.gym.member.Status;

import java.time.LocalDate;

public record MemberSimpleDto(Long id, String fullName, String email, LocalDate membershipStartDate, Status status, String planName, String gymName) {
}
