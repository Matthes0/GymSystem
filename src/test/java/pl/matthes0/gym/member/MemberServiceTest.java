package pl.matthes0.gym.member;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.matthes0.gym.member.dtos.MemberCreateDto;
import pl.matthes0.gym.member.dtos.MemberDetailsDto;
import pl.matthes0.gym.member.dtos.MemberSimpleDto;
import pl.matthes0.gym.membershipplan.MembershipPlan;
import pl.matthes0.gym.membershipplan.MembershipPlanRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MembershipPlanRepository membershipPlanRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberService service;

    @Test
    void shouldRegisterMemberSuccessfully() {
        Long planId = 1L;
        MembershipPlan plan = new MembershipPlan();
        plan.setId(planId);
        plan.setMaxMembers(10);

        MemberCreateDto createDto = new MemberCreateDto("Janusz Fitness", "janusz@gym.pl");
        Member member = new Member();
        Member savedMember = new Member();
        MemberDetailsDto expectedDto = mock(MemberDetailsDto.class);

        when(membershipPlanRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(memberRepository.countByMembershipPlanIdAndStatus(planId, Status.ACTIVE)).thenReturn(5L);
        when(memberMapper.toEntity(createDto)).thenReturn(member);
        when(memberRepository.save(member)).thenReturn(savedMember);
        when(memberMapper.toDetailsDto(savedMember)).thenReturn(expectedDto);

        MemberDetailsDto result = service.registerNewMember(planId, createDto);

        assertNotNull(result);
        assertEquals(Status.ACTIVE, member.getStatus());
        assertNotNull(member.getMembershipStartDate());
        verify(memberRepository).save(member);
    }

    @Test
    void shouldThrowBadRequestWhenPlanIsFull() {
        Long planId = 1L;
        MembershipPlan plan = new MembershipPlan();
        plan.setId(planId);
        plan.setMaxMembers(10);

        MemberCreateDto createDto = new MemberCreateDto("Pudzian", "mario@pudzian.pl");

        when(membershipPlanRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(memberRepository.countByMembershipPlanIdAndStatus(planId, Status.ACTIVE)).thenReturn(10L);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registerNewMember(planId, createDto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assert exception.getReason() != null;
        assertTrue(exception.getReason().contains("already full"));
        verify(memberRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundWhenPlanDoesNotExist() {
        Long planId = 999L;
        when(membershipPlanRepository.findById(planId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registerNewMember(planId, mock(MemberCreateDto.class)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void shouldCancelMembershipSuccessfully() {
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        member.setStatus(Status.ACTIVE);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.save(member)).thenReturn(member);
        when(memberMapper.toDetailsDto(member)).thenReturn(mock(MemberDetailsDto.class));

        service.cancelMembership(memberId);

        assertEquals(Status.CANCELLED, member.getStatus());
        verify(memberRepository).save(member);
    }

    @Test
    void shouldReturnAllMembers() {
        Member member = new Member();
        when(memberRepository.findAll()).thenReturn(List.of(member));
        when(memberMapper.toSimpleDto(member)).thenReturn(mock(MemberSimpleDto.class));

        List<MemberSimpleDto> result = service.getAllMembers();

        assertEquals(1, result.size());
        verify(memberRepository).findAll();
    }

    @Test
    void shouldThrowNotFoundWhenCancellingNonExistentMember() {
        Long nonExistentMemberId = 999L;

        when(memberRepository.findById(nonExistentMemberId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.cancelMembership(nonExistentMemberId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(memberRepository, never()).save(any());
        assert exception.getReason() != null;
        assertTrue(exception.getReason().contains("not found"));
    }
}