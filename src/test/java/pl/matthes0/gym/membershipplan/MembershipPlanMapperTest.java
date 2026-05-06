package pl.matthes0.gym.membershipplan;

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

    private static final String PLAN_NAME = "Basic Plan";
    private static final BigDecimal AMOUNT = new BigDecimal("100.00");
    private static final String CURRENCY_CODE = "PLN";

    @Test
    void shouldMapDtoToEntity() {
        Price price = new Price(AMOUNT, Currency.getInstance(CURRENCY_CODE));
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto(PLAN_NAME, Plan.BASIC, price, 1, 100);

        MembershipPlan result = mapper.toEntity(dto);

        assertAll("Entity mapping",
                () -> assertNotNull(result),
                () -> assertEquals(PLAN_NAME, result.getName()),
                () -> assertEquals(Plan.BASIC, result.getPlan()),
                () -> assertEquals(price, result.getMonthlyPrice()),
                () -> assertEquals(1, result.getDurationInMonths()),
                () -> assertEquals(100, result.getMaxMembers()),
                () -> assertNull(result.getId())
        );
    }

    @Test
    void shouldMapEntityToDetailsDto() {
        Gym gym = new Gym();
        gym.setName("Power Gym");
        Price price = new Price(AMOUNT, Currency.getInstance(CURRENCY_CODE));

        MembershipPlan plan = createPlan(1L, PLAN_NAME, Plan.BASIC, price, 1, 100, gym);

        GymDetailsDto gymDto = new GymDetailsDto(1L, "Power Gym", "Address", "123");
        when(gymMapper.toDetailsDto(gym)).thenReturn(gymDto);

        MembershipPlanDetailsDto result = mapper.toDetailsDto(plan);

        assertAll("DetailsDto mapping",
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(PLAN_NAME, result.name()),
                () -> assertEquals(Plan.BASIC, result.plan()),
                () -> assertEquals(price, result.monthlyPrice()),
                () -> assertEquals(gymDto, result.gymDetailsDto())
        );
    }

    @Test
    void shouldReturnNullWhenInputsAreNull() {
        assertAll("Null handling",
                () -> assertNull(mapper.toEntity(null)),
                () -> assertNull(mapper.toDetailsDto(null))
        );
    }

    @Test
    void shouldMapEntityWithPartialData() {
        MembershipPlan plan = new MembershipPlan();
        plan.setId(5L);
        plan.setName("No Gym Plan");

        when(gymMapper.toDetailsDto(null)).thenReturn(null);

        MembershipPlanDetailsDto result = mapper.toDetailsDto(plan);

        assertAll("Partial data mapping",
                () -> assertNotNull(result),
                () -> assertEquals(5L, result.id()),
                () -> assertEquals("No Gym Plan", result.name()),
                () -> assertNull(result.monthlyPrice()),
                () -> assertNull(result.gymDetailsDto())
        );
    }


    private MembershipPlan createPlan(Long id, String name, Plan type, Price price, int duration, int maxMembers, Gym gym) {
        MembershipPlan plan = new MembershipPlan();
        plan.setId(id);
        plan.setName(name);
        plan.setPlan(type);
        plan.setMonthlyPrice(price);
        plan.setDurationInMonths(duration);
        plan.setMaxMembers(maxMembers);
        plan.setGym(gym);
        return plan;
    }
}