package pl.matthes0.gym.membershipplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.matthes0.gym.gym.Gym;
import pl.matthes0.gym.gym.GymRepository;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanCreateDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanDetailsDto;

@Service
public class MembershipPlanService {
    private final MembershipPlanRepository membershipPlanRepository;
    private final GymRepository gymRepository;

    @Autowired
    public MembershipPlanService(MembershipPlanRepository membershipPlanRepository, GymRepository gymRepository){
        this.membershipPlanRepository = membershipPlanRepository;
        this.gymRepository = gymRepository;
    }

    public MembershipPlanDetailsDto createMembershipPlan(Long gymId, MembershipPlanCreateDto membershipPlanDto){
        Gym gym = gymRepository.findById(gymId).orElse(new Gym());
        MembershipPlan membershipPlan = new MembershipPlan();
        membershipPlan.setName(membershipPlanDto.name());
        membershipPlan.setMonthlyPrice(membershipPlanDto.monthlyPrice());
        membershipPlan.setDurationInMonths(membershipPlanDto.durationInMonths());
        membershipPlan.setMaxMembers(membershipPlanDto.maxMembers());
        membershipPlan.setGym(gym);
        membershipPlanRepository.save(membershipPlan);
        return new MembershipPlanDetailsDto(
                membershipPlan.getId(),
                membershipPlan.getName(),
                membershipPlan.getPlan(),
                membershipPlan.getMonthlyPrice(),
                membershipPlan.getDurationInMonths(),
                membershipPlan.getMaxMembers(),
                new GymDetailsDto(gym.getId(),
                        gym.getName(),
                        gym.getAddress(),
                        gym.getPhoneNumber())
        );
    }
}
