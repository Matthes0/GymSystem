package pl.matthes0.gym.gym;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.matthes0.gym.gym.dtos.GymCreateDto;
import pl.matthes0.gym.gym.dtos.GymDetailsDto;
import pl.matthes0.gym.gym.dtos.RevenueReportDto;
import pl.matthes0.gym.membershipplan.Price;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymServiceTest {

    @Mock private GymRepository repository;
    @Mock private GymMapper mapper;
    @InjectMocks private GymService service;

    private static final String NAME = "Iron Paradise";
    private static final String ADDRESS = "Miami";
    private static final String PHONE = "123456789";

    @Test
    void shouldCreateGymSuccessfully() {
        GymCreateDto dto = new GymCreateDto(NAME, ADDRESS, PHONE);
        Gym gym = new Gym();
        Gym savedGym = new Gym();
        savedGym.setId(1L);
        GymDetailsDto expectedDto = new GymDetailsDto(1L, NAME, ADDRESS, PHONE);

        when(repository.existsByName(NAME)).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(gym);
        when(repository.save(gym)).thenReturn(savedGym);
        when(mapper.toDetailsDto(savedGym)).thenReturn(expectedDto);

        GymDetailsDto result = service.createGym(dto);

        assertEquals(expectedDto, result);
        verify(repository).save(gym);
    }

    @Test
    void shouldReturnAllGyms() {
        Gym gym = new Gym();
        GymDetailsDto detailsDto = new GymDetailsDto(1L, NAME, ADDRESS, PHONE);

        when(repository.findAll()).thenReturn(List.of(gym));
        when(mapper.toDetailsDto(gym)).thenReturn(detailsDto);

        List<GymDetailsDto> result = service.findAllGyms();

        assertAll("Find all gyms",
                () -> assertEquals(1, result.size()),
                () -> assertEquals(detailsDto, result.get(0))
        );
        verify(repository).findAll();
    }

    @Test
    void shouldThrowConflictExceptionWhenNameExists() {
        GymCreateDto dto = new GymCreateDto(NAME, ADDRESS, PHONE);
        when(repository.existsByName(NAME)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.createGym(dto));

        assertAll("Conflict exception details",
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatusCode()),
                () -> {
                    assert exception.getReason() != null;
                    assertTrue(exception.getReason().contains("already exists"));
                }
        );

        verify(repository, never()).save(any());
        verify(mapper, never()).toEntity(any());
    }

    @Test
    void shouldReturnRevenueReport() {
        List<RevenueReportDto> report = List.of(
                new RevenueReportDto("Gym 1", new Price(new BigDecimal("297.01"), Currency.getInstance("PLN"))),
                new RevenueReportDto("Gym 1", new Price(new BigDecimal("20.99"), Currency.getInstance("EUR")))
        );
        when(repository.getRevenueReport()).thenReturn(report);

        List<RevenueReportDto> result = service.getRevenue();

        assertAll("Revenue report",
                () -> assertEquals(2, result.size()),
                () -> assertEquals(report, result)
        );
        verify(repository).getRevenueReport();
    }
}