package pl.matthes0.gym.membershipplan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.matthes0.gym.gym.Gym;
import pl.matthes0.gym.gym.GymRepository;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanCreateDto;
import pl.matthes0.gym.membershipplan.dtos.MembershipPlanDetailsDto;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipPlanServiceTest {

    @Mock
    private MembershipPlanRepository membershipPlanRepository;

    @Mock
    private GymRepository gymRepository;

    @Mock
    private MembershipPlanMapper membershipPlanMapper;

    @InjectMocks
    private MembershipPlanService service;

    private static final Long GYM_ID = 1L;
    private static final Long PLAN_ID = 10L;
    private static final String PLAN_NAME = "Pro Plan";
    private static final BigDecimal PRICE_AMOUNT = new BigDecimal("150.00");
    private static final String CURRENCY_CODE = "PLN";

    @Test
    void shouldCreateMembershipPlanSuccessfully() {
        Gym gym = createGym(GYM_ID);
        Price price = new Price(PRICE_AMOUNT, Currency.getInstance(CURRENCY_CODE));
        MembershipPlanCreateDto createDto = new MembershipPlanCreateDto(PLAN_NAME, Plan.PREMIUM, price, 12, 100);

        MembershipPlan membershipPlan = new MembershipPlan();
        MembershipPlan savedPlan = new MembershipPlan();
        savedPlan.setId(PLAN_ID);

        MembershipPlanDetailsDto expectedDto = new MembershipPlanDetailsDto(
                PLAN_ID, PLAN_NAME, Plan.PREMIUM, price, 12, 100, null);

        when(gymRepository.findById(GYM_ID)).thenReturn(Optional.of(gym));
        when(membershipPlanMapper.toEntity(createDto)).thenReturn(membershipPlan);
        when(membershipPlanRepository.save(membershipPlan)).thenReturn(savedPlan);
        when(membershipPlanMapper.toDetailsDto(savedPlan)).thenReturn(expectedDto);

        MembershipPlanDetailsDto result = service.createMembershipPlan(GYM_ID, createDto);

        assertAll("Membership plan creation",
                () -> assertNotNull(result),
                () -> assertEquals(expectedDto, result),
                () -> assertEquals(gym, membershipPlan.getGym())
        );
        verify(gymRepository).findById(GYM_ID);
        verify(membershipPlanRepository).save(membershipPlan);
    }

    @Test
    void shouldThrowNotFoundWhenGymDoesNotExistDuringCreation() {
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto("Name", Plan.BASIC, null, 1, 10);
        when(gymRepository.findById(GYM_ID)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.createMembershipPlan(GYM_ID, dto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(membershipPlanRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllPlansForGym() {
        MembershipPlan plan1 = new MembershipPlan();
        MembershipPlan plan2 = new MembershipPlan();
        MembershipPlanDetailsDto dto1 = mock(MembershipPlanDetailsDto.class);
        MembershipPlanDetailsDto dto2 = mock(MembershipPlanDetailsDto.class);

        when(gymRepository.existsById(GYM_ID)).thenReturn(true);
        when(membershipPlanRepository.findByGymId(GYM_ID)).thenReturn(List.of(plan1, plan2));
        when(membershipPlanMapper.toDetailsDto(plan1)).thenReturn(dto1);
        when(membershipPlanMapper.toDetailsDto(plan2)).thenReturn(dto2);

        List<MembershipPlanDetailsDto> result = service.getAllMembershipPlans(GYM_ID);

        assertAll("Get all plans success",
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()),
                () -> assertEquals(dto1, result.get(0)),
                () -> assertEquals(dto2, result.get(1))
        );
        verify(gymRepository).existsById(GYM_ID);
        verify(membershipPlanRepository).findByGymId(GYM_ID);
    }

    @Test
    void shouldThrowNotFoundWhenGymDoesNotExistDuringGetAll() {
        when(gymRepository.existsById(GYM_ID)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.getAllMembershipPlans(GYM_ID));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(membershipPlanRepository, never()).findByGymId(any());
    }


    private Gym createGym(Long id) {
        Gym gym = new Gym();
        gym.setId(id);
        return gym;
    }
}