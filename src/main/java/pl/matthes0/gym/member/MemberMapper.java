package pl.matthes0.gym.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.matthes0.gym.member.dtos.MemberCreateDto;
import pl.matthes0.gym.member.dtos.MemberDetailsDto;
import pl.matthes0.gym.membershipplan.MembershipPlan;
import pl.matthes0.gym.membershipplan.MembershipPlanMapper;

@Component
@RequiredArgsConstructor
public class MemberMapper {
    private final MembershipPlanMapper membershipPlanMapper;

    public Member toEntity(MemberCreateDto memberCreateDto, MembershipPlan membershipPlan) {
        if (memberCreateDto == null) {
            return null;
        }
        Member member = new Member();
        member.setFullName(memberCreateDto.fullName());
        member.setEmail(memberCreateDto.email());
        member.setMembershipPlan(membershipPlan);
        return member;
    }
    public MemberDetailsDto toDetailsDto(Member member){
        if (member == null){
            return null;
        }
        return new MemberDetailsDto(
                member.getId(),
                member.getFullName(),
                member.getEmail(),
                member.getMembershipStartDate(),
                member.getStatus(),
                membershipPlanMapper.toDetailsDto(member.getMembershipPlan())
        );
    }
}
