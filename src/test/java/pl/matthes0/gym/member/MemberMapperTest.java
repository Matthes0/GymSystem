package pl.matthes0.gym.member;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.matthes0.gym.gym.Gym;
import pl.matthes0.gym.member.dtos.MemberCreateDto;
import pl.matthes0.gym.member.dtos.MemberDetailsDto;
import pl.matthes0.gym.member.dtos.MemberSimpleDto;
import pl.matthes0.gym.membershipplan.MembershipPlan;
import pl.matthes0.gym.membershipplan.MembershipPlanMapper;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanDetailsDto;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberMapperTest {

    @Mock
    private MembershipPlanMapper membershipPlanMapper;

    @InjectMocks
    private MemberMapper mapper;

    private static final String FULL_NAME = "Jan Kowalski";
    private static final String EMAIL = "jan@test.com";
    private static final Long MEMBER_ID = 1L;

    @Test
    void shouldMapDtoToEntity() {
        MemberCreateDto dto = new MemberCreateDto(FULL_NAME, EMAIL);

        Member result = mapper.toEntity(dto);

        assertAll("Member mapping",
                () -> assertNotNull(result),
                () -> assertEquals(FULL_NAME, result.getFullName()),
                () -> assertEquals(EMAIL, result.getEmail()),
                () -> assertNull(result.getId())
        );
    }

    @Test
    void shouldMapEntityToDetailsDto() {
        MembershipPlan plan = new MembershipPlan();
        Member member = createMember(MEMBER_ID, FULL_NAME, EMAIL, Status.ACTIVE, LocalDate.now(), plan);

        MembershipPlanDetailsDto planDto = new MembershipPlanDetailsDto(1L, "Plan", null, null, 1, 10, null);
        when(membershipPlanMapper.toDetailsDto(plan)).thenReturn(planDto);

        MemberDetailsDto result = mapper.toDetailsDto(member);

        assertAll("MemberDetailsDto mapping",
                () -> assertNotNull(result),
                () -> assertEquals(MEMBER_ID, result.id()),
                () -> assertEquals(FULL_NAME, result.fullName()),
                () -> assertEquals(planDto, result.membershipPlanDetailsDto())
        );
    }

    @Test
    void shouldMapEntityToSimpleDto() {
        Gym gym = new Gym();
        gym.setName("Power Gym");

        MembershipPlan plan = new MembershipPlan();
        plan.setName("Basic Plan");
        plan.setGym(gym);

        Member member = createMember(MEMBER_ID, FULL_NAME, EMAIL, Status.ACTIVE, LocalDate.now(), plan);

        MemberSimpleDto result = mapper.toSimpleDto(member);

        assertAll("MemberSimpleDto mapping",
                () -> assertNotNull(result),
                () -> assertEquals(MEMBER_ID, result.id()),
                () -> assertEquals(FULL_NAME, result.fullName()),
                () -> assertEquals("Basic Plan", result.planName()),
                () -> assertEquals("Power Gym", result.gymName())
        );
    }

    @Test
    void shouldReturnNullWhenInputsAreNull() {
        assertAll("Null handling",
                () -> assertNull(mapper.toEntity(null)),
                () -> assertNull(mapper.toDetailsDto(null)),
                () -> assertNull(mapper.toSimpleDto(null))
        );
    }

    @Test
    void shouldHandleMemberEntityWithNullFieldsInDetailsDto() {
        Member member = createMember(99L, "Minimalist Member", EMAIL, Status.ACTIVE, LocalDate.now(), null);

        when(membershipPlanMapper.toDetailsDto(null)).thenReturn(null);

        MemberDetailsDto result = mapper.toDetailsDto(member);

        assertAll("Partial data mapping",
                () -> assertNotNull(result),
                () -> assertEquals(99L, result.id()),
                () -> assertNull(result.membershipPlanDetailsDto())
        );
    }


    private Member createMember(Long id, String name, String email, Status status, LocalDate date, MembershipPlan plan) {
        Member member = new Member();
        member.setId(id);
        member.setFullName(name);
        member.setEmail(email);
        member.setStatus(status);
        member.setMembershipStartDate(date);
        member.setMembershipPlan(plan);
        return member;
    }
}