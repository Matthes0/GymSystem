package pl.matthes0.gym.member;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.matthes0.gym.member.dtos.MemberCreateDto;
import pl.matthes0.gym.member.dtos.MemberDetailsDto;
import pl.matthes0.gym.member.dtos.MemberSimpleDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/membership-plans/{id}/members")
    public ResponseEntity<MemberDetailsDto> registerNewMember(@PathVariable Long id, @RequestBody @Valid MemberCreateDto memberDto) {
        MemberDetailsDto createdMember = memberService.registerNewMember(id, memberDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberSimpleDto>> getAllMembers() {
        List<MemberSimpleDto> allMembersList = memberService.getAllMembers();
        return ResponseEntity.ok(allMembersList);
    }

    @PatchMapping("/members/{id}/cancel")
    public ResponseEntity<MemberDetailsDto> cancelMembership(@PathVariable Long id) {
        MemberDetailsDto cancelledMember = memberService.cancelMembership(id);
        return ResponseEntity.ok(cancelledMember);
    }
}
