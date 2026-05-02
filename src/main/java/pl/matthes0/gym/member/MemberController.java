package pl.matthes0.gym.member;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/membership-plans/{membershipPlanId}/members")
public class MemberController {
    private final MemberService memberService;

//    public MemberDetailsDto registerNewMember(@PathVariable Long membershipPlanId, @RequestBody @Valid MemberCreateDto memberDto){
//        return memberService.registerNewMember(membershipPlanId, memberDto);
//    }
}
