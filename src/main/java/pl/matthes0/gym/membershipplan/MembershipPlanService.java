package pl.matthes0.gym.membershipplan;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.matthes0.gym.gym.Gym;
import pl.matthes0.gym.gym.GymRepository;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanCreateDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanDetailsDto;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MembershipPlanService {
    private final MembershipPlanRepository membershipPlanRepository;
    private final GymRepository gymRepository;
    private final MembershipPlanMapper membershipPlanMapper;

    @Transactional
    public MembershipPlanDetailsDto createMembershipPlan(Long gymId, MembershipPlanCreateDto membershipPlanDto){
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new NoSuchElementException("Gym with id " + gymId + " not found"));
        MembershipPlan membershipPlan = membershipPlanMapper.toEntity(membershipPlanDto, gym);
        MembershipPlan savedPlan = membershipPlanRepository.save(membershipPlan);
        return membershipPlanMapper.toDetailsDto(savedPlan);
    }
    public List<MembershipPlanDetailsDto> getAllMembershipPlans(Long gymId){
        if (!gymRepository.existsById(gymId)) {
            throw new NoSuchElementException("Gym with id " + gymId + " not found");
        }
        return membershipPlanRepository.findAllByGymId(gymId).stream()
                .map(membershipPlanMapper::toDetailsDto)
                .toList();
    }
}
