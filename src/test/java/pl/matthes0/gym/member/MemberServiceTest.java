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

    private static final Long PLAN_ID = 1L;
    private static final Long MEMBER_ID = 10L;
    private static final String NAME = "Janusz Fitness";
    private static final String EMAIL = "janusz@gym.pl";
    private static final int MAX_MEMBERS = 10;

    @Test
    void shouldRegisterMemberSuccessfully() {
        MembershipPlan plan = createPlan(PLAN_ID, MAX_MEMBERS);
        MemberCreateDto createDto = new MemberCreateDto(NAME, EMAIL);
        Member member = new Member();
        Member savedMember = new Member();
        MemberDetailsDto expectedDto = mock(MemberDetailsDto.class);

        when(membershipPlanRepository.findById(PLAN_ID)).thenReturn(Optional.of(plan));
        when(memberRepository.countByMembershipPlanIdAndStatus(PLAN_ID, Status.ACTIVE)).thenReturn(5L);
        when(memberMapper.toEntity(createDto)).thenReturn(member);
        when(memberRepository.save(member)).thenReturn(savedMember);
        when(memberMapper.toDetailsDto(savedMember)).thenReturn(expectedDto);

        MemberDetailsDto result = service.registerNewMember(PLAN_ID, createDto);

        assertAll("Registration success",
                () -> assertNotNull(result),
                () -> assertEquals(Status.ACTIVE, member.getStatus()),
                () -> assertNotNull(member.getMembershipStartDate())
        );
        verify(memberRepository).save(member);
    }

    @Test
    void shouldThrowBadRequestWhenPlanIsFull() {
        MembershipPlan plan = createPlan(PLAN_ID, MAX_MEMBERS);
        MemberCreateDto createDto = new MemberCreateDto(NAME, EMAIL);

        when(membershipPlanRepository.findById(PLAN_ID)).thenReturn(Optional.of(plan));
        when(memberRepository.countByMembershipPlanIdAndStatus(PLAN_ID, Status.ACTIVE)).thenReturn((long) MAX_MEMBERS);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registerNewMember(PLAN_ID, createDto));

        assertAll("Plan full exception",
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode()),
                () -> {
                    assert exception.getReason() != null;
                    assertTrue(exception.getReason().contains("already full"));
                }
        );
        verify(memberRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundWhenPlanDoesNotExist() {
        when(membershipPlanRepository.findById(PLAN_ID)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.registerNewMember(PLAN_ID, new MemberCreateDto(NAME, EMAIL)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void shouldCancelMembershipSuccessfully() {
        Member member = createMember(MEMBER_ID, Status.ACTIVE);

        when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
        when(memberRepository.save(member)).thenReturn(member);
        when(memberMapper.toDetailsDto(member)).thenReturn(mock(MemberDetailsDto.class));

        service.cancelMembership(MEMBER_ID);

        assertEquals(Status.CANCELLED, member.getStatus());
        verify(memberRepository).save(member);
    }

    @Test
    void shouldReturnAllMembers() {
        Member member = new Member();
        MemberSimpleDto simpleDto = mock(MemberSimpleDto.class);

        when(memberRepository.findAll()).thenReturn(List.of(member));
        when(memberMapper.toSimpleDto(member)).thenReturn(simpleDto);

        List<MemberSimpleDto> result = service.getAllMembers();

        assertAll("Get all members",
                () -> assertEquals(1, result.size()),
                () -> assertEquals(simpleDto, result.get(0))
        );
        verify(memberRepository).findAll();
    }

    @Test
    void shouldThrowNotFoundWhenCancellingNonExistentMember() {
        when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.cancelMembership(MEMBER_ID));

        assertAll("Member not found exception",
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode()),
                () -> {
                    assert exception.getReason() != null;
                    assertTrue(exception.getReason().contains("not found"));
                }
        );
        verify(memberRepository, never()).save(any());
    }


    private MembershipPlan createPlan(Long id, int maxMembers) {
        MembershipPlan plan = new MembershipPlan();
        plan.setId(id);
        plan.setMaxMembers(maxMembers);
        return plan;
    }

    private Member createMember(Long id, Status status) {
        Member member = new Member();
        member.setId(id);
        member.setStatus(status);
        return member;
    }
}