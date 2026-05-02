package pl.matthes0.gym.membershipplan.dtos;

import pl.matthes0.gym.membershipplan.Plan;
import pl.matthes0.gym.membershipplan.Price;

public record MembershipPlanCreateDto(String name, Plan plan, Price monthlyPrice, Integer durationInMonths, Integer maxMembers) {
}
