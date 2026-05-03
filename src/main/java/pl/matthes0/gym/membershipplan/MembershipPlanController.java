package pl.matthes0.gym.membershipplan;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<MembershipPlanDetailsDto> createMembershipPlan(@PathVariable Long gymId, @RequestBody MembershipPlanCreateDto membershipPlanDto){
        MembershipPlanDetailsDto createdMembershipPlan = membershipPlanService.createMembershipPlan(gymId, membershipPlanDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMembershipPlan);
    }
    @GetMapping
    public ResponseEntity<List<MembershipPlanDetailsDto>> getAllMembershipPlans(@PathVariable Long gymId){
        List<MembershipPlanDetailsDto> allMembershipPlans = membershipPlanService.getAllMembershipPlans(gymId);
        return ResponseEntity.ok(allMembershipPlans);
    }

}
