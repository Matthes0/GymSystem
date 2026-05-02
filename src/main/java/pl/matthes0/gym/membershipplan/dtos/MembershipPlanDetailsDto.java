package pl.matthes0.gym.membershipplan.dtos;

import pl.matthes0.gym.gym.dtos.GymDetailsDto;
import pl.matthes0.gym.membershipplan.Plan;
import pl.matthes0.gym.membershipplan.Price;

public record MembershipPlanDetailsDto(
        Long id,
        String name,
        Plan plan,
        Price monthlyPrice,
        Integer durationInMonths,
        Integer maxMembers,
        GymDetailsDto gymDetailsDto) {
}
