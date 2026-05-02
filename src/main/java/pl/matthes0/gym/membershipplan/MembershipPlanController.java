package pl.matthes0.gym.membershipplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanCreateDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanDetailsDto;

@RestController
@RequestMapping("/api/memberships")
public class MembershipPlanController {

    private final MembershipPlanService membershipPlanService;

    @Autowired
    public MembershipPlanController(MembershipPlanService membershipPlanService) {
        this.membershipPlanService = membershipPlanService;
    }

//    @PostMapping
//    public MembershipPlanDetailsDto addMembership(@PathVariable Long gymId, @RequestBody MembershipPlanCreateDto membershipDto){
//
//    }

}
