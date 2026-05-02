package pl.matthes0.gym.member;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.matthes0.gym.member.dtos.MemberCreateDto;
import pl.matthes0.gym.member.dtos.MemberDetailsDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/membership-plans/{membershipPlanId}/members")
    public MemberDetailsDto registerNewMember(@PathVariable Long membershipPlanId, @RequestBody @Valid MemberCreateDto memberDto){
        return memberService.registerNewMember(membershipPlanId, memberDto);
    }
    @GetMapping("/members")
    public List<MemberDetailsDto> getAllMembers(){
        return memberService.getAllMembers();
    }
    @PatchMapping("/members/{id}/cancel")
    public MemberDetailsDto cancelMembership(@PathVariable Long id){
        return memberService.cancelMembership(id);
    }

}
