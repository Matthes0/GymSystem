package pl.matthes0.gym.member;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<MemberDetailsDto> registerNewMember(@PathVariable Long membershipPlanId, @RequestBody @Valid MemberCreateDto memberDto){
        MemberDetailsDto createdMember = memberService.registerNewMember(membershipPlanId, memberDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }
    @GetMapping("/members")
    public ResponseEntity<List<MemberDetailsDto>> getAllMembers(){
        List<MemberDetailsDto> allMembersList = memberService.getAllMembers();
        return ResponseEntity.ok(allMembersList);
    }
    @PatchMapping("/members/{id}/cancel")
    public ResponseEntity<MemberDetailsDto> cancelMembership(@PathVariable Long id){
        MemberDetailsDto cancelledMember = memberService.cancelMembership(id);
        return ResponseEntity.ok(cancelledMember);
    }
}
