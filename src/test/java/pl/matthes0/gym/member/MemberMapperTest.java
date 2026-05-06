package pl.matthes0.gym.member;

import org.junit.jupiter.api.DisplayName;
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

    // success test cases

    @Test
    @DisplayName("Should correctly map MemberCreateDto to Member entity")
    void shouldMapDtoToEntity() {
        MemberCreateDto dto = new MemberCreateDto("Jan Kowalski", "jan@test.com");

        Member result = mapper.toEntity(dto);

        assertNotNull(result);
        assertEquals("Jan Kowalski", result.getFullName());
        assertEquals("jan@test.com", result.getEmail());
        assertNull(result.getId());
    }

    @Test
    @DisplayName("Should correctly map Member entity to MemberDetailsDto")
    void shouldMapEntityToDetailsDto() {
        MembershipPlan plan = new MembershipPlan();
        Member member = new Member();
        member.setId(1L);
        member.setFullName("Jan Kowalski");
        member.setEmail("jan@test.com");
        member.setMembershipStartDate(LocalDate.now());
        member.setStatus(Status.ACTIVE);
        member.setMembershipPlan(plan);

        MembershipPlanDetailsDto planDto = new MembershipPlanDetailsDto(1L, "Plan", null, null, 1, 10, null);
        when(membershipPlanMapper.toDetailsDto(plan)).thenReturn(planDto);

        MemberDetailsDto result = mapper.toDetailsDto(member);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Jan Kowalski", result.fullName());
        assertEquals(planDto, result.membershipPlanDetailsDto());
    }

    @Test
    @DisplayName("Should correctly map Member entity to MemberSimpleDto")
    void shouldMapEntityToSimpleDto() {
        Gym gym = new Gym();
        gym.setName("Power Gym");

        MembershipPlan plan = new MembershipPlan();
        plan.setName("Basic Plan");
        plan.setGym(gym);

        Member member = new Member();
        member.setId(1L);
        member.setFullName("Jan Kowalski");
        member.setEmail("jan@test.com");
        member.setMembershipStartDate(LocalDate.now());
        member.setStatus(Status.ACTIVE);
        member.setMembershipPlan(plan);

        MemberSimpleDto result = mapper.toSimpleDto(member);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Jan Kowalski", result.fullName());
        assertEquals("Basic Plan", result.planName());
        assertEquals("Power Gym", result.gymName());
    }


    @Test
    @DisplayName("Should return null when mapping null MemberCreateDto")
    void shouldReturnNullWhenToEntityInputIsNull() {
        Member result = mapper.toEntity(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when mapping null Member entity to DetailsDto")
    void shouldReturnNullWhenToDetailsDtoInputIsNull() {
        MemberDetailsDto result = mapper.toDetailsDto(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when mapping null Member entity to SimpleDto")
    void shouldReturnNullWhenToSimpleDtoInputIsNull() {
        MemberSimpleDto result = mapper.toSimpleDto(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Should handle Member entity with null fields correctly in DetailsDto")
    void shouldMapEntityWithPartialDataToDetailsDto() {
        Member member = new Member();
        member.setId(99L);
        member.setFullName("Minimalist Member");
        member.setMembershipPlan(null);

        when(membershipPlanMapper.toDetailsDto(null)).thenReturn(null);

        MemberDetailsDto result = mapper.toDetailsDto(member);

        assertNotNull(result);
        assertEquals(99L, result.id());
        assertNull(result.membershipPlanDetailsDto());
    }
}