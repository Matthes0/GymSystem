package pl.matthes0.gym.membershipplan;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanCreateDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanDetailsDto;

import java.util.List;

@RestController
@RequestMapping("/api/gyms/{gymId}/membership-plans")
@RequiredArgsConstructor
public class MembershipPlanController {
    private final MembershipPlanService membershipPlanService;

    @PostMapping
    public MembershipPlanDetailsDto createMembershipPlan(@PathVariable Long gymId, @RequestBody MembershipPlanCreateDto membershipPlanDto){
        return membershipPlanService.createMembershipPlan(gymId, membershipPlanDto);
    }
    @GetMapping
    public List<MembershipPlanDetailsDto> getAllMembershipPlans(@PathVariable Long gymId){
        return membershipPlanService.getAllMembershipPlans(gymId);
    }

}
