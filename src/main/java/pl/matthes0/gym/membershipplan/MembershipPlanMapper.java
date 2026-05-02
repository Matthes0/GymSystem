package pl.matthes0.gym.membershipplan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.matthes0.gym.gym.Gym;
import pl.matthes0.gym.gym.GymMapper;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanCreateDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanDetailsDto;

@Component
@RequiredArgsConstructor
public class MembershipPlanMapper {

    private final GymMapper gymMapper;

    public MembershipPlan toEntity(MembershipPlanCreateDto membershipPlanCreateDto){
        if (membershipPlanCreateDto == null){
            return null;
        }

        MembershipPlan membershipPlan = new MembershipPlan();
        membershipPlan.setName(membershipPlanCreateDto.name());
        membershipPlan.setPlan(membershipPlanCreateDto.plan());
        membershipPlan.setMonthlyPrice(membershipPlanCreateDto.monthlyPrice());
        membershipPlan.setDurationInMonths(membershipPlanCreateDto.durationInMonths());
        membershipPlan.setMaxMembers(membershipPlanCreateDto.maxMembers());

        return membershipPlan;
    }

    public MembershipPlanDetailsDto toDetailsDto(MembershipPlan membershipPlan){
        if (membershipPlan == null) {
            return null;
        }
        return new MembershipPlanDetailsDto(
                membershipPlan.getId(),
                membershipPlan.getName(),
                membershipPlan.getPlan(),
                membershipPlan.getMonthlyPrice(),
                membershipPlan.getDurationInMonths(),
                membershipPlan.getMaxMembers(),
                gymMapper.toDetailsDto(membershipPlan.getGym())
        );
    }

}
