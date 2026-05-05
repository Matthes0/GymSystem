package pl.matthes0.gym.membershipplan;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.matthes0.gym.gym.Gym;
import pl.matthes0.gym.gym.GymMapper;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanCreateDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanDetailsDto;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipPlanMapperTest {

    @Mock
    private GymMapper gymMapper;

    @InjectMocks
    private MembershipPlanMapper mapper;


    @Test
    @DisplayName("Should correctly map MembershipPlanCreateDto to MembershipPlan entity")
    void shouldMapDtoToEntity() {
        Price price = new Price(new BigDecimal("100.00"), Currency.getInstance("PLN"));
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto("Basic Plan", Plan.BASIC, price, 1, 100);

        MembershipPlan result = mapper.toEntity(dto);

        assertNotNull(result);
        assertEquals("Basic Plan", result.getName());
        assertEquals(Plan.BASIC, result.getPlan());
        assertEquals(price, result.getMonthlyPrice());
        assertEquals(1, result.getDurationInMonths());
        assertEquals(100, result.getMaxMembers());
        assertNull(result.getId());
    }

    @Test
    @DisplayName("Should correctly map MembershipPlan entity to MembershipPlanDetailsDto")
    void shouldMapEntityToDetailsDto() {
        Gym gym = new Gym();
        gym.setName("Power Gym");
        Price price = new Price(new BigDecimal("100.00"), Currency.getInstance("PLN"));

        MembershipPlan plan = new MembershipPlan();
        plan.setId(1L);
        plan.setName("Basic Plan");
        plan.setPlan(Plan.BASIC);
        plan.setMonthlyPrice(price);
        plan.setDurationInMonths(1);
        plan.setMaxMembers(100);
        plan.setGym(gym);

        GymDetailsDto gymDto = new GymDetailsDto(1L, "Power Gym", "Address", "123");
        when(gymMapper.toDetailsDto(gym)).thenReturn(gymDto);

        MembershipPlanDetailsDto result = mapper.toDetailsDto(plan);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Basic Plan", result.name());
        assertEquals(Plan.BASIC, result.plan());
        assertEquals(price, result.monthlyPrice());
        assertEquals(gymDto, result.gymDetailsDto());
    }


    @Test
    @DisplayName("Should return null when mapping null MembershipPlanCreateDto")
    void shouldReturnNullWhenToEntityInputIsNull() {
        MembershipPlan result = mapper.toEntity(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when mapping null MembershipPlan entity")
    void shouldReturnNullWhenToDetailsDtoInputIsNull() {
        MembershipPlanDetailsDto result = mapper.toDetailsDto(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Should handle MembershipPlan entity with null fields correctly")
    void shouldMapEntityWithPartialData() {
        MembershipPlan plan = new MembershipPlan();
        plan.setId(5L);
        plan.setName("No Gym Plan");
        when(gymMapper.toDetailsDto(null)).thenReturn(null);

        MembershipPlanDetailsDto result = mapper.toDetailsDto(plan);

        assertNotNull(result);
        assertEquals(5L, result.id());
        assertEquals("No Gym Plan", result.name());
        assertNull(result.monthlyPrice());
        assertNull(result.gymDetailsDto());
    }
}