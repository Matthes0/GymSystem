package pl.matthes0.gym.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.matthes0.gym.member.dtos.MemberCreateDto;
import pl.matthes0.gym.member.dtos.MemberDetailsDto;
import pl.matthes0.gym.membershipplan.MembershipPlan;
import pl.matthes0.gym.membershipplan.MembershipPlanRepository;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final MemberMapper memberMapper;

    @Transactional
    public MemberDetailsDto registerNewMember(Long membershipPlanId, MemberCreateDto memberCreateDto) {
        MembershipPlan membershipPlan = membershipPlanRepository.findById(membershipPlanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership plan with id " + membershipPlanId + " not found"));
        long activeMembersCount = memberRepository.countByMembershipPlanIdAndStatus(membershipPlanId, Status.ACTIVE);
        if (activeMembersCount >= membershipPlan.getMaxMembers()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This membership plan is already full");
        }

        Member member = memberMapper.toEntity(memberCreateDto);
        member.setMembershipPlan(membershipPlan);
        member.setMembershipStartDate(LocalDate.now());
        member.setStatus(Status.ACTIVE);

        Member savedMember = memberRepository.save(member);
        return memberMapper.toDetailsDto(savedMember);
    }

    public List<MemberDetailsDto> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(memberMapper::toDetailsDto)
                .toList();
    }
    @Transactional
    public MemberDetailsDto cancelMembership(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member with id " + id + " not found"));
        member.setStatus(Status.CANCELLED);
        Member cancelledMember = memberRepository.save(member);
        return memberMapper.toDetailsDto(cancelledMember);
    }
}
