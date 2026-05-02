package pl.matthes0.gym.membershipplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanCreateDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanDetailsDto;

import java.util.List;

@RestController
@RequestMapping("/api/gyms/{gymId}/membership-plans")
public class MembershipPlanController {

    private final MembershipPlanService membershipPlanService;

    @Autowired
    public MembershipPlanController(MembershipPlanService membershipPlanService) {
        this.membershipPlanService = membershipPlanService;
    }
    @PostMapping
    public MembershipPlanDetailsDto createMembershipPlan(@PathVariable Long gymId, @RequestBody MembershipPlanCreateDto membershipDto){
        return membershipPlanService.createMembershipPlan(gymId, membershipDto);
    }
    @GetMapping
    public List<MembershipPlanDetailsDto> getAllMembershipPlans(@PathVariable Long gymId){
        return membershipPlanService.getAllMembershipPlans(gymId);
    }

}
