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

    @Test
    void shouldCreateMembershipPlanSuccessfully() {
        Long gymId = 1L;
        Gym gym = new Gym();
        gym.setId(gymId);

        Price price = new Price(new BigDecimal("150.00"), Currency.getInstance("PLN"));
        MembershipPlanCreateDto createDto = new MembershipPlanCreateDto(
                "Pro Plan", Plan.PREMIUM, price, 12, 100);

        MembershipPlan membershipPlan = new MembershipPlan();
        MembershipPlan savedPlan = new MembershipPlan();
        savedPlan.setId(10L);

        MembershipPlanDetailsDto expectedDto = new MembershipPlanDetailsDto(
                10L, "Pro Plan", Plan.PREMIUM, price, 12, 100, null);

        when(gymRepository.findById(gymId)).thenReturn(Optional.of(gym));
        when(membershipPlanMapper.toEntity(createDto)).thenReturn(membershipPlan);
        when(membershipPlanRepository.save(membershipPlan)).thenReturn(savedPlan);
        when(membershipPlanMapper.toDetailsDto(savedPlan)).thenReturn(expectedDto);

        MembershipPlanDetailsDto result = service.createMembershipPlan(gymId, createDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(gymRepository).findById(gymId);
        verify(membershipPlanRepository).save(membershipPlan);
        assertEquals(gym, membershipPlan.getGym());
    }

    @Test
    void shouldThrowNotFoundWhenGymDoesNotExistDuringCreation() {
        Long gymId = 999L;
        MembershipPlanCreateDto dto = new MembershipPlanCreateDto(
                "Name", Plan.BASIC, null, 1, 10);

        when(gymRepository.findById(gymId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.createMembershipPlan(gymId, dto));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(membershipPlanRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllPlansForGym() {
        Long gymId = 1L;
        MembershipPlan plan1 = new MembershipPlan();
        MembershipPlan plan2 = new MembershipPlan();

        MembershipPlanDetailsDto dto1 = mock(MembershipPlanDetailsDto.class);
        MembershipPlanDetailsDto dto2 = mock(MembershipPlanDetailsDto.class);

        when(gymRepository.existsById(gymId)).thenReturn(true);
        when(membershipPlanRepository.findByGymId(gymId)).thenReturn(List.of(plan1, plan2));
        when(membershipPlanMapper.toDetailsDto(plan1)).thenReturn(dto1);
        when(membershipPlanMapper.toDetailsDto(plan2)).thenReturn(dto2);

        List<MembershipPlanDetailsDto> result = service.getAllMembershipPlans(gymId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(gymRepository).existsById(gymId);
        verify(membershipPlanRepository).findByGymId(gymId);
        verify(membershipPlanMapper, times(2)).toDetailsDto(any(MembershipPlan.class));
    }

    @Test
    void shouldThrowNotFoundWhenGymDoesNotExistDuringGetAll() {
        Long gymId = 999L;
        when(gymRepository.existsById(gymId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.getAllMembershipPlans(gymId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(membershipPlanRepository, never()).findByGymId(any());
    }
}