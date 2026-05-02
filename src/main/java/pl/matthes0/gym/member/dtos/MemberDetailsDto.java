package pl.matthes0.gym.member.dtos;

import pl.matthes0.gym.member.Status;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanDetailsDto;

import java.time.LocalDate;

public record MemberDetailsDto(
        Long id,
        String fullName,
        String email,
        LocalDate membershipStartDate,
        Status status,
        MembershipPlanDetailsDto membershipPlanDetailsDto) {
}
