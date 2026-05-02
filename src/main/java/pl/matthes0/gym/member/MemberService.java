package pl.matthes0.gym.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.matthes0.gym.member.dtos.MemberCreateDto;
import pl.matthes0.gym.membershipplan.MembershipPlanRepository;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MembershipPlanRepository membershipPlanRepository;

    public void registerNewMember(Long membershipPlanId, MemberCreateDto memberCreateDto) {

    }
}
